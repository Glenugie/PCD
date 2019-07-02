% Groups Master List
% Group(g1,peer1).
:- dynamic group/2.
	
% Peer Master List
% peer(peer1).
:- dynamic peer/1.

% Data Master List
:- dynamic data/4.
		
% polRejected(FactOwner, PolicyHolder, Rejector, DataID)
:- dynamic polRejected/4.

% State of Affairs
	%myGPS(245).

	% Obligations
	% obligation(ObligationOwner, Obligation).
	% obligation(peer1,[peer1,provideRecords(500),23445566]).
	:- dynamic obligation/2.
		
	% Record Requests/Accesses
	% recordRequest(RecordOwner, RequestorID, DataID, Quantity, Date, RequestApproved).
	% recordRequest(peer1,peer1,d2,500,date(2016,11,15,9,30,0,0,'UTC',false),true).
	:- dynamic recordRequest/6.
	
	% Data
	% hasData(FactOwner, DataOwner, DataID, Quantity).
	:- dynamic hasData/4.
	% noData(FactOwner, PeerID, DataID)
	:- dynamic noData/3.
	% connected(FactOwner, PeerID)
	:- dynamic connected/2.
	
% findData :- connected/2 entries which do not have noData/3 entries
findData(I,D,L) :- hasData(I,_,D,_),!,findall(ID,hasData(I,ID,D,_),L).
findData(I,D,L) :- findall(ID,possibleData(I,ID,D),L).

possibleData(I,ID,D) :- connected(I,ID), neg(noData(I,ID,D)).
	%numRecords(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,T,_,true),S), listTotal(S,0,Z).

% Policies
% policy(PolicyOwner,PolicyID,Policy).
% policy(peer1,1,['P',peer42,d1,[recordsAccessed(_ < 50)],[]]).
:- dynamic policy/3.

%:- ['JavaOutput.pl'].

% Permission Checkers
	/* Order of Policy Permission Checks:
		F Peer Data, P Peer Data, F Peer any, P Peer any
		F Group Data, P Group Data, F Group any, P Group any
		F any Data, P any Data, F any any, P any any
	*/
	% I is the owner, X is the requestor ID, Y is the data ID, L is the policy (usually a variable)
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'F'), checkIdentity(L,X), checkData(L,Y), checkConditions(L), checkObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'P'), checkIdentity(L,X), checkData(L,Y), checkConditions(L), checkObligations(L),!.
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'F'), checkIdentity(L,X), checkAnyData(I,L,Y), checkConditions(L), checkObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'P'), checkIdentity(L,X), checkAnyData(I,L,Y), checkConditions(L), checkObligations(L),!.

	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'F'), checkGroupIdentity(L,X), checkData(L,Y), checkConditions(L), checkObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'P'), checkGroupIdentity(L,X), checkData(L,Y), checkConditions(L), checkObligations(L),!.
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'F'), checkGroupIdentity(L,X), checkAnyData(I,L,Y), checkConditions(L), checkObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,_,L), checkModality(L,'P'), checkGroupIdentity(L,X), checkAnyData(I,L,Y), checkConditions(L), checkObligations(L),!.

	permit(I,_,Y,L) :- policy(I,_,L), checkModality(L,'F'), checkAnyIdentity(L), checkData(L,Y), checkConditions(L), checkObligations(L),!,fail.
	permit(I,_,Y,L) :- policy(I,_,L), checkModality(L,'P'), checkAnyIdentity(L), checkData(L,Y), checkConditions(L), checkObligations(L),!.
	permit(I,_,Y,L) :- policy(I,_,L), checkModality(L,'F'), checkAnyIdentity(L), checkAnyData(I,L,Y), checkConditions(L), checkObligations(L),!,fail.
	permit(I,_,Y,L) :- policy(I,_,L), checkModality(L,'P'), checkAnyIdentity(L), checkAnyData(I,L,Y), checkConditions(L), checkObligations(L),!.

	% permit if no policies exist defaultPermit("T"). At this point, execution has stopped if there is an explicit permit or forbid, yes?
	permit(I,X,Y,L) :- defaultPermit(I,'T'), L= ['P',X,Y,[],[]].
	
	relPolicies(I,X,Y,L) :- policy(I,_,L), checkIdentity(L,X), checkData(L,Y).
	relPolicies(I,X,_,L) :- policy(I,_,L), checkIdentity(L,X), checkAnyDataNoExist(L).
	relPolicies(I,X,Y,L) :- policy(I,_,L), checkGroupIdentity(L,X), checkData(L,Y).
	relPolicies(I,X,_,L) :- policy(I,_,L), checkGroupIdentity(L,X), checkAnyDataNoExist(L).
	relPolicies(I,_,Y,L) :- policy(I,_,L), checkAnyIdentity(L), checkData(L,Y).
	relPolicies(I,_,_,L) :- policy(I,_,L), checkAnyIdentity(L), checkAnyDataNoExist(L).
	
	%relRecords(I,X,[M,ID,D,C,O],R) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D,Q,T,G), R = recordRequest(I,I,D,Q,T,G).
	% Need to capture data and peers mentioned in conditions/obligations

	relRecords(I,X,[M,ID,D,C,O],R) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D2,Q,T,G), R = recordRequest(I,I,D2,Q,T,G). % Reports ALL records as relevant

	%relRecords(I,X,[M,ID,D,C,O],PID) :- policy(X,_,[M,ID,D,C,O]), PID = policy(X,_,[M,ID,D,C,O]).
	%relRecords(I,X,[M,ID,D,C,O],ID2,D2,Q,T,G) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D,Q,T,G), ID2 = ID, D2 = D.

% Modality, Identity, and Data Checks
	checkModality([M|_],X) :- =(M,X).

	checkIdentity([_,ID|_],X) :- =(ID,X).
	checkGroupIdentity([_,ID|_],X) :- group(G,X), =(G,ID).
	checkAnyIdentity([_,ID|_]) :- =(ID,any).

	%checkIdentitySingle(ID,X) :- =(ID,X).
	%checkIdentitySingle(ID,X) :- group(G,X), =(G,ID).
	%checkIdentitySingle(ID,_) :- =(ID,any).

	checkData([_,_,D|_],X) :- =(D,X).
	checkAnyDataNoExist([_,_,D|_]) :- =(D,any).
	checkAnyData(I,[_,_,D|_],X) :- =(D,any), hasData(I,I,X,_).

% Condition Checking
	/* Potential Conditions
		recordsAccessed([Peer,Data],Num,[Date])
		recordsRequested([Peer,Data],Num,[Date])
		requests([Peer,Data],Num,[Date])

		lastRequest([Peer,Data],Date)
		lastAccess([Peer,Data],Date)

		year()
		month()
		day()
		hour()
		minute()
	*/
	checkConditions([_,_,D,C|_]) :- checkCondition(C,D).
		:- discontiguous checkCondition/2.
		% Records Accessed
			checkCondition([recordsAccessed(CV)|T],D) :- me(ID), conRecordsAccessed(ID,D,CV), checkCondition(T,D).
			checkCondition([recordsAccessed(ID,D2,CV)|T],D) :- conRecordsAccessed(ID,D2,CV), checkCondition(T,D).
			%conRecordsAccessed(ID,D,C) :- integer(C), numRecords(ID,D,T), T < C.
			conRecordsAccessed(ID,D,C) :- numRecords(ID,D,T), conditionComparator(T,C,<).

		% Records Requested
			checkCondition([recordsRequested(CV)|T],D) :- me(ID), conRecordsRequested(ID,D,CV), checkCondition(T,D).
			checkCondition([recordsRequested(ID,D2,CV)|T],D) :- conRecordsRequested(ID,D2,CV), checkCondition(T,D).
			conRecordsRequested(X,Y,C) :- numRecordsRequested(X,Y,T), conditionComparator(T,C,<).

		% Requests
			checkCondition([requests(CV)|T],D) :- me(ID), conRequests(ID,D,CV), checkCondition(T,D).
			checkCondition([requests(ID,D2,CV)|T],D) :- conRequests(ID,D2,CV), checkCondition(T,D).
			conRequests(X,Y,C) :- numRequests(X,Y,T), conditionComparator(T,C,<).

		% Last Request
			checkCondition([lastRequest(CV)|T],D) :- me(ID), conLastRequest(ID,D,CV), checkCondition(T,D).
			checkCondition([lastRequest(ID,D2,CV)|T],D) :- conLastRequest(ID,D2,CV), checkCondition(T,D).	
			%conLastRequest(X,Y,V) :- group(X,_), !, findall(M,group(X,M),GMs), findall(T,recordRequest(GMs,Y,_,T,_),S), maxDate(S,M), date_time_stamp(V,VAc), M =< VAc, !, fail.
			conLastRequest(X,Y,V) :- findall(T,recordRequest(X,Y,_,T,_),S), maxDate(S,M), date_time_stamp(V,VAc), conditionComparator(M,VAc,=<).

		% Last Access
			checkCondition([lastAccess(CV)|T],D) :- me(ID), conLastAccess(ID,D,CV), checkCondition(T,D).
			checkCondition([lastAccess(ID,D2,CV)|T],D) :- conLastAccess(ID,D2,CV), checkCondition(T,D).	
			conLastAccess(X,Y,V) :- findall(T,recordRequest(X,Y,_,T,true),S), maxDate(S,M), date_time_stamp(V,VAc),  conditionComparator(M,VAc,=<).

		% Date/Time
			checkCondition([year(CV)|T],D) :- conYear(CV), checkCondition(T,D).
			conYear(V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('year',D,VC), =(V,VC).

			checkCondition([month(CV)|T],D) :- conMonth(CV), checkCondition(T,D).
			conMonth(V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('month',D,VC), =(V,VC).

			checkCondition([day(CV)|T],D) :- conDay(CV), checkCondition(T,D).
			conDay(V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('day',D,VC), =(V,VC).

			checkCondition([hour(CV)|T],D) :- conHour(CV), checkCondition(T,D).
			conHour(V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('hour',D,VC), =(V,VC).

			checkCondition([minute(CV)|T],D) :- conMinute(CV), checkCondition(T,D).
			conMinute(V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('minute',D,VC), =(V,VC).
	checkCondition([],_).

% Obligation Checking
	/* Potential Obligations
		provide(Target,Data,Quantity,Peer)
		enforce(Target,Policy)
	*/
	checkObligations([_,_,_,_,O|_]) :- checkObligation(O).
	:- discontiguous checkObligation/1.
		%provide(Target,Data,Quantity,Peer)
			checkObligation([provide(Tgt,D,Q,ID)|T]) :- oblProvide(Tgt,D,Q,ID), checkObligation(T).
			oblProvide(_,_,_,_).

		%enforce(Target,Policy)
			checkObligation([enforce(Tgt,P)|T]) :- policy(P,_), oblEnforce(Tgt,P), checkObligation(T).
			oblEnforce(_,_).
	checkObligation([]).

% Queries
	listTrueConditions(L) :- policy(_,L), checkConditions(L).
	listTrueObligations(L) :- policy(_,L), checkObligations(L).

	whatCanIAccess(Y) :- me(X), policy(_,L), permit(X,Y,L).
	whoCanAccess(X,Y) :- peer(X), permit(X,Y,_).
	whatCanTheyAccess(I,X,Y) :- data(Y,_,_,_), permit(I,X,Y,_).
	whoCanAccessWhat(X,Y) :- peer(X), data(Y,_,_,_), permit(X,Y,_).

	numRecords(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,T,_,true),S), listTotal(S,0,Z).
	numRecordsRequested(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,T,_,_),S), listTotal(S,0,Z).
	numRequests(I,X,Y,Z) :- findall(X,recordRequest(I,X,Y,_,_,_),S), listLength(S,0,Z).
	numRequestsApproved(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,T,_,true),S), listLength(S,0,Z).
	numRequestsDenied(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,T,_,false),S), listLength(S,0,Z).
	getLastRequest(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,_,T,_),S), maxDate(S,M), stamp_date_time(M,Z,'UTC').
	getLastAccess(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,_,T,true),S), maxDate(S,M), stamp_date_time(M,Z,'UTC').

	recordObligations(L) :- [_,_,_,_,O|_] = L, recordObligation(O).
		recordObligation([provide(Tgt,D,Q,ID)|T]) :- write(obligation([Tgt,provide(D,Q,ID)])),nl, recordObligation(T).
		recordObligation([enforce(Tgt,P)|T]) :- policy(P,L), write(obligation([Tgt,enforce(L)])),nl, recordObligation(T).
	recordObligation([]).

% Utility
	listTotal([H|T],A,L) :- Anew is A+H, listTotal(T,Anew,L). 
	listTotal([],A,A).
	listLength([_|T],A,L) :- Anew is A+1, listLength(T,Anew,L). 
	listLength([],A,A).

	accMaxDate([H|T],A,Max) :- date_time_stamp(H,Ti), Ti  >  A, accMaxDate(T,Ti,Max). 
	accMaxDate([H|T],A,Max) :- date_time_stamp(H,Ti), Ti  =<  A, accMaxDate(T,A,Max). 
	accMaxDate([],A,A).
	maxDate(List,Max) :- List = [H|_], date_time_stamp(H,Ti), accMaxDate(List,Ti,Max).

	conditionComparator(C1,T,_) :- complexTerm(T), functor(T,C,_), arg(2,T,C2), compareNumbers(C1,C,C2).
	conditionComparator(C1,T,C) :- number(T), compareNumbers(C1,C,T).
	compareNumbers(C1,C,C2) :- functor(T,C,2), arg(1,T,C1), arg(2,T,C2), T.
	complexTerm(X) :- nonvar(X), functor(X,_,A), A > 0.

	greaterThanZero(X) :- ( X > 0 -> true;  false).
	neg(G) :- G,!,fail. 
   	neg(_).

% Could have a system where you submit a request of the form (Peer, Data, Quantity), and it returns the generated records you are permitted to access
	% requestData(I,ID,D,N,M) :- permit(I,ID,D,L), !, requestRecords(I,ID,D,L,M,N), get_time(T), stamp_date_time(T,Date,'UTC'), write(recordRequest(ID,ID,D,M,Date,true)), nl, recordObligations(L), sendRecords(D,M,M).
	requestData(I,ID,D,N,R) :- permit(I,ID,D,L), !, requestRecords(I,ID,D,L,M,N), get_time(T), stamp_date_time(T,Date,'UTC'), R = recordRequest(ID,ID,D,M,Date,true), recordObligations(L).
	% recordRequest(ID,D,M,Date,true)

	requestRecords(I,ID,D,L,T,N) :- maxRecords(I,ID,D,L,M), M =:= -1, !, T = N.
	requestRecords(I,ID,D,L,T,N) :- maxRecords(I,ID,D,L,M), M >= N, T = N.
	requestRecords(I,ID,D,L,T,N) :- maxRecords(I,ID,D,L,M), M < N, T = M.
	maxRecords(I,ID,D,L,M) :- [_,_,_,C|_] = L, maxRecord(I,ID,C,D,M), M >= -1.
	maxRecord(I,ID,[recordsAccessed(CV)|_],D,M) :- !,findMaxRecord(I,ID,D,CV,M).
	maxRecord(I,ID,[recordsAccessed(ID,D,CV)|_],D,M) :- !,findMaxRecord(I,ID,D,CV,M).
	maxRecord(_,_,[_|_],_,M) :- M = -1.
	maxRecord(_,_,[],_,M) :- M = -1.
	findMaxRecord(I,ID,D,CV,M) :- numRecords(I,ID,D,R), functor(CV,C,_), C = <, arg(2,CV,V), M is V - R.
	findMaxRecord(I,ID,D,CV,M) :- numRecords(I,ID,D,R), functor(CV,C,_), C = =<, arg(2,CV,V), M is V - R.

	sendRecords(D,M,T) :- M > 0, C is T - M + 1, write(C:[D,'-------------------']), nl, MNew is M - 1, sendRecords(D,MNew,T). 
	sendRecords(_,M,_) :- M =< 0.