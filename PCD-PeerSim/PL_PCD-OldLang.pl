% Dynamic Fact Intialisers
	% Groups Master List: group(g1,peer1).
	:- dynamic group/2.

	% Peer Master List: peer(peer1).
	:- dynamic peer/1.

	% Data Master List: data(Data ID, DataParent, LinkedDataConcept, EncodedData)
	:- dynamic data/4.

	% polRejected(FactOwner, PolicyHolder, Rejector, DataID)
	:- dynamic polRejected/4.

	% Obligations: obligation(ObligationOwner, Obligation). -> obligation(peer1,[peer1,provideRecords(500),23445566]).
	:- dynamic obligation/2.

	% Record Requests/Accesses: recordRequest(RecordOwner, ProviderID, RequestorID, DataID, Quantity, Date, RequestApproved). -> recordRequest(peer1, peer0, peer1, d7, 1, date('date(2017', '1', '25', '14', '33', '50.400300979', '0', ''UTC'', ''-')'), true).
	:- dynamic recordRequest/7.

	% Data Ownership: hasData(FactOwner, DataOwner, DataID).
	:- dynamic hasData/3.

	% Data Elements/Records: dataElement(DataOwner, DataID, DataBody).
	:- dynamic dataElement/3.

	% noData(FactOwner, PeerID, DataID)
	:- dynamic noData/3.

	% connected(FactOwner, PeerID)
	:- dynamic connected/2.

	% peerOffline(FactOwner, PeerID)
	:- dynamic peerOffline/2.

	% defaultPermit(FactOwner, 'T' or 'F')
	:- dynamic defaultPermit/2.

	% Policies: policy(PolicyOwner,Policy). -> policy(peer1,[POL]).
	% [ 'P' , peer42 , d1 , [ [recordsAccessed(peer1,d1) < 50] , 0 , 0 ] , [] , [], false]
	:- dynamic policy/2.

	% I should not request D from ID until cycle T
	% noRequest(FactOwner, ProviderID, Data, CycleToRevoke)
	:- dynamic noRequest/4.

% Reference to the Java simulation's prolog intialisers (policies, peers, data, etc.)
%:- ['JavaOutput.pl'].

% Permission Checkers
	% F Peer Data, 	P Peer Data, 	F Peer any, 	P Peer any
	% F Group Data, P Group Data, 	F Group any, 	P Group any
	% F any Data, 	P any Data, 	F any any, 		P any any

% Checks if I's current (self-targetting) policies prevent requesting D from ID, and returns matching policy(s)
	permitRequest(I,ID,D,_) :- noRequest(I,ID,D,_),!,fail.
	permitRequest(I,ID,D,L) :- policy(I,L), L=['F',ID,D,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	permitRequest(I,ID,D,L) :- policy(I,L), L=['P',ID,D,_,_,_,true,_,_], checkConditions(I,L),!.
	permitRequest(I,ID,_,L) :- policy(I,L), L=['F',ID,any,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	permitRequest(I,ID,_,L) :- policy(I,L), L=['P',ID,any,_,_,_,true,_,_], checkConditions(I,L),!.
	permitRequest(I,ID,D,L) :- policy(I,L), L=['F',G,D,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!,fail.
	permitRequest(I,ID,D,L) :- policy(I,L), L=['P',G,D,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!.
	permitRequest(I,ID,_,L) :- policy(I,L), L=['F',G,any,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!,fail.
	permitRequest(I,ID,_,L) :- policy(I,L), L=['P',G,any,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!.
	permitRequest(I,_,D,L) :- policy(I,L), L=['F',any,D,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	permitRequest(I,_,D,L) :- policy(I,L), L=['P',any,D,_,_,_,true,_,_], checkConditions(I,L),!.
	permitRequest(I,_,_,L) :- policy(I,L), L=['F',any,any,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	permitRequest(I,_,_,L) :- policy(I,L), L=['P',any,any,_,_,_,true,_,_], checkConditions(I,L),!.
	permitRequest(_,ID,D,L) :- L= ['P',ID,D,[],[],[],true,_,_].

% Checks if a specific (self-targetting) policy (L) prevents I from requesting D from anyone
	permitRequestPolicy(I,D,L) :- L=['F',_,D,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	permitRequestPolicy(I,D,L) :- L=['P',_,D,_,_,_,true,_,_], checkConditions(I,L),!.
	permitRequestPolicy(I,_,L) :- L=['F',_,any,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	permitRequestPolicy(I,_,L) :- L=['P',_,any,_,_,_,true,_,_], checkConditions(I,L),!.
	permitRequestPolicy(_,_,_).

% Checks if I's current policies prevent providing D to ID, and returns matching policy(s)
	permit(I,ID,D,L) :- policy(I,L), L=['F',ID,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	permit(I,ID,D,L) :- policy(I,L), L=['P',ID,D,_,_,_,false,_,_], checkConditions(I,L),!.
	permit(I,ID,D,L) :- policy(I,L), L=['F',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	permit(I,ID,D,L) :- policy(I,L), L=['P',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	permit(I,ID,D,L) :- policy(I,L), L=['F',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!,fail.
	permit(I,ID,D,L) :- policy(I,L), L=['P',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!.
	permit(I,ID,D,L) :- policy(I,L), L=['F',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!,fail.
	permit(I,ID,D,L) :- policy(I,L), L=['P',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!.
	permit(I,_,D,L) :- policy(I,L), L=['F',any,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	permit(I,_,D,L) :- policy(I,L), L=['P',any,D,_,_,_,false,_,_], checkConditions(I,L),!.
	permit(I,_,D,L) :- policy(I,L), L=['F',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	permit(I,_,D,L) :- policy(I,L), L=['P',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	permit(I,ID,D,L) :- defaultPermit(I,'T'), L= ['P',ID,D,[],[],[],false,_,_].

% Checks if a specific policy (L) forbids I from providing D to ID
	permitPolicy(I,ID,D,L) :- L=['F',ID,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	permitPolicy(I,ID,D,L) :- L=['P',ID,D,_,_,_,false,_,_], checkConditions(I,L),!.
	permitPolicy(I,ID,D,L) :- L=['F',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	permitPolicy(I,ID,D,L) :- L=['P',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	permitPolicy(I,ID,D,L) :- L=['F',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!,fail.
	permitPolicy(I,ID,D,L) :- L=['P',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!.
	permitPolicy(I,ID,D,L) :- L=['F',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!,fail.
	permitPolicy(I,ID,D,L) :- L=['P',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!.
	permitPolicy(I,_,D,L) :- L=['F',any,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	permitPolicy(I,_,D,L) :- L=['P',any,D,_,_,_,false,_,_], checkConditions(I,L),!.
	permitPolicy(I,_,D,L) :- L=['F',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	permitPolicy(I,_,D,L) :- L=['P',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	permitPolicy(_,_,_,_).

% Gathers List of Relevant Policies
	relPolicies(I,ID,D,L) :- policy(I,L), L=[_,ID,D,_,_,_,false,_,_].
	relPolicies(I,ID,_,L) :- policy(I,L), L=[_,ID,any,_,_,_,false,_,_].
	relPolicies(I,ID,D,L) :- policy(I,L), L=[_,G,D,_,_,_,false,_,_], group(G,ID).
	relPolicies(I,ID,_,L) :- policy(I,L), L=[_,G,any,_,_,_,false,_,_], group(G,ID).
	relPolicies(I,_,D,L) :- policy(I,L), L=[_,any,D,_,_,_,false,_,_].
	relPolicies(I,_,_,L) :- policy(I,L), L=[_,any,any,_,_,_,false,_,_].

% Checks if a policy is relevant (to be called on receipt of relPolicies, this is used by the requestor to filter out irrelevant policies based on their records)
	policyRelevant(I,L) :- checkConditions(I,L).

% Gathers List of Relevant Records
	% Need to capture data and peers mentioned in conditions/obligations
	%relRecords(I,X,[M,ID,D,C,O],R) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D,Q,T,G), R = recordRequest(I,I,D,Q,T,G).
	relRecords(I,X,[M,_,RID,D,C,PO,O],R) :- policy(X,[M,RID,D,C,PO,O,_,_,_]), recordRequest(I,X,I,D2,Q,T,G), R = recordRequest(I,X,I,D2,Q,T,G). % Reports ALL records as relevant
	%relRecords(I,X,[M,ID,D,C,O],PID) :- policy(X,_,[M,ID,D,C,O]), PID = policy(X,_,[M,ID,D,C,O]).
	%relRecords(I,X,[M,ID,D,C,O],ID2,D2,Q,T,G) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D,Q,T,G), ID2 = ID, D2 = D.


%  polsViolatedBy(I, Pol, L): Returns policies (self or otherwise) held by I violated by Pol
polsViolatedBy(I,Pol,L) :- policy(I,L), polViolates(I,Pol,L).

% polsViolates(I, Pol1, Pol2): Returns true if Pol2 conflicts with Pol1
polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,any,_,_,_,S1,_,_], Pol2=[_,ID2,_,_,_,_,S2,_,_], S1=S2, ID1=ID2. 							% ID: Any=Any,Group=Group,Peer=Peer. Data: Any=Any,Any=Data
polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,D1,_,_,_,S1,_,_], Pol2=[_,ID2,D2,_,_,_,S2,_,_], S1=S2, ID1=ID2, D1=D2. 						% ID: Any=Any,Group=Group,Peer=Peer. Data: Any=Any,Data=Data
polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,any,_,_,_,S1,_,_], Pol2=[_,_,_,_,_,_,S2,_,_], S1=S2, group(ID1,_). 							% ID: Group=Peer. Data: Any=Any,Any=Data
polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,D1,_,_,_,S1,_,_], Pol2=[_,_,D2,_,_,_,S2,_,_], S1=S2, group(ID1,_), D1=D2. 					% ID: Group=Peer. Data: Any=Any,Data=Data
%polViolates(I,Pol1,Pol2) :- Pol1=[_,ID1,any,_,_,_,S1,_,_], Pol2=[_,ID2,_,_,_,_,S2,_,_], S1=S2, group(ID1,I), group(ID2,I). 		% ID: Shared Group Data: Any=Any,Any=Data
%polViolates(I,Pol1,Pol2) :- Pol1=[_,ID1,D1,_,_,_,S1,_,_], Pol2=[_,ID2,D2,_,_,_,S2,_,_], S1=S2, group(ID1,I), group(ID2,I), D1=D2. 	% ID: Shared Group Data: Any=Any,Data=Data
polViolates(_,Pol1,Pol2) :- Pol1=[_,any,any,_,_,_,S1,_,_], Pol2=[_,_,_,_,_,_,S2,_,_], S1=S2. 										% ID: Any=Any,Any=Group,Any=Peer. Data: Any=Any,Any=Data
polViolates(_,Pol1,Pol2) :- Pol1=[_,any,D1,_,_,_,S1,_,_], Pol2=[_,_,D2,_,_,_,S2,_,_], S1=S2, D1=D2. 								% ID: Any=Any,Any=Group,Any=Peer. Data: Any=Any,Data=Data

% Finding Confirmed or Possible Data
	% findData :- connected/2 entries which do not have noData/3 entries
	findData(I,D,L) :- hasData(I,_,D),!,findall(ID,hasData(I,ID,D),L).
	findData(I,D,L) :- findall(ID,possibleData(I,ID,D),L).
	possibleData(I,ID,D) :- connected(I,ID), neg(noData(I,ID,D)).
	numData(I,D,Z) :- findall(T,dataElement(I,D,T),S), listLength(S,0,Z).

	numWithoutData(I,D,Z) :- findall(ID,neighbourWithoutData(I,ID,D),S), listLength(S,0,Z). 
	neighbourWithoutData(I,ID,D) :- connected(I,ID), noData(I,ID,D).

% Condition Checking
	checkConditions(I,[_,_,D,C|_]) :- checkCondition(I,C,D).
		:- discontiguous checkCondition/3.
		% Records Accessed: recordsAccessed(Peer,Data) Operator Num
			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,recordsAccessed(ID,D2)), arg(2,H,V), conRecordsAccessed(I,ID,D2,Comp,V), checkCondition(I,T,D).
			conRecordsAccessed(I,ID,D,C,V) :- numRecords(I,_,ID,D,T), conditionComparator(T,V,C).

		% Records Requested: recordsRequested(Peer,Data) Operator Num
			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,recordsRequested(ID,D2)), arg(2,H,V), conRecordsRequested(I,ID,D2,Comp,V), checkCondition(I,T,D).
			conRecordsRequested(I,ID,D,C,V) :- numRecordsRequested(I,_,ID,D,T), conditionComparator(T,V,C).

		% Requests: requests(Peer,Data) Operator Num
			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,requests(ID,D2)), arg(2,H,V), conRequests(I,ID,D2,Comp,V), checkCondition(I,T,D).
			conRequests(I,ID,D,C,V) :- numRequests(I,_,ID,D,T), conditionComparator(T,V,C).

		% Last Request lastRequest(Peer,Data) Operator Date
			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,lastRequest(ID,D2)), arg(2,H,V), conLastRequest(I,ID,D2,Comp,V), checkCondition(I,T,D).
			conLastRequest(I,ID,D,C,V) :- findall(T,recordRequest(I,_,ID,D,_,T,_),S), maxDate(S,M), date_time_stamp(V,VAc), conditionComparator(M,VAc,C).

		% Last Access lastAccess(Peer,Data) Operator Date
			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,lastAccess(ID,D2)), arg(2,H,V), conLastAccess(I,ID,D2,Comp,V), checkCondition(I,T,D).
			conLastAccess(I,ID,D,C,V) :- findall(T,recordRequest(I,_,ID,D,_,T,true),S), maxDate(S,M), date_time_stamp(V,VAc), conditionComparator(M,VAc,C).

		% Date/Time: year/month/day/hour/minute() Operator Num
			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,year()), arg(2,H,Y), conYear(Comp,Y), checkCondition(I,T,D).
			conYear(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('year',D,VC), conditionComparator(V,VC,C).

			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,month()), arg(2,H,M), conMonth(Comp,M), checkCondition(I,T,D).
			conMonth(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('month',D,VC), conditionComparator(V,VC,C).

			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,day()), arg(2,H,Da), conDay(Comp,Da), checkCondition(I,T,D).
			conDay(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('day',D,VC), conditionComparator(V,VC,C).

			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,hour()), arg(2,H,Ho), conHour(Comp,Ho), checkCondition(I,T,D).
			conHour(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('hour',D,VC), conditionComparator(V,VC,C).

			checkCondition(I,[H|T],D) :- functor(H,Comp,_), arg(1,H,minute()), arg(2,H,M), conMinute(Comp,M), checkCondition(I,T,D).
			conMinute(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('minute',D,VC), conditionComparator(V,VC,C).
	checkCondition(_,[],_).


% Queries (Associated with Conditions)
	numRecords(I,PID,RID,Y,Z) :- findall(T,recordRequest(I,PID,RID,Y,T,_,true),S), listTotal(S,0,Z).
	numRecordsRequested(I,PID,RID,Y,Z) :- findall(T,recordRequest(I,PID,RID,Y,T,_,_),S), listTotal(S,0,Z).
	numRequests(I,PID,RID,Y,Z) :- findall(RID,recordRequest(I,PID,RID,Y,_,_,_),S), listLength(S,0,Z).
	numRequestsApproved(I,PID,RID,Y,Z) :- findall(T,recordRequest(I,PID,RID,Y,T,_,true),S), listLength(S,0,Z).
	numRequestsDenied(I,PID,RID,Y,Z) :- findall(T,recordRequest(I,PID,RID,Y,T,_,false),S), listLength(S,0,Z).
	getLastRequest(I,PID,RID,Y,Z) :- findall(T,recordRequest(I,PID,RID,Y,_,T,_),S), maxDate(S,M), stamp_date_time(M,Z,'UTC').
	getLastAccess(I,PID,RID,Y,Z) :- findall(T,recordRequest(I,PID,RID,Y,_,T,true),S), maxDate(S,M), stamp_date_time(M,Z,'UTC').

% Utility
	% Computes the total of the elemnents in a given list
	listTotal([H|T],A,L) :- Anew is A+H, listTotal(T,Anew,L).
	listTotal([],A,A).
	listLength([_|T],A,L) :- Anew is A+1, listLength(T,Anew,L).
	listLength([],A,A).

	% Finds the transaction record with the most recent date for a given collection of transaction records
	accMaxDate([H|T],A,Max) :- date_time_stamp(H,Ti), Ti  >  A, accMaxDate(T,Ti,Max).
	accMaxDate([H|T],A,Max) :- date_time_stamp(H,Ti), Ti  =<  A, accMaxDate(T,A,Max).
	accMaxDate([],A,A).
	maxDate(List,Max) :- List = [H|_], date_time_stamp(H,Ti), accMaxDate(List,Ti,Max).

	% Compares two numers using a comparator (as an argument)
	conditionComparator(C1,T,_) :- complexTerm(T), functor(T,C,_), arg(2,T,C2), compareNumbers(C1,C,C2).
	conditionComparator(C1,T,C) :- number(T), compareNumbers(C1,C,T).
	compareNumbers(C1,C,C2) :- functor(T,C,2), arg(1,T,C1), arg(2,T,C2), T.
	complexTerm(X) :- nonvar(X), functor(X,_,A), A > 0.

	% Negates a given fact
	neg(G) :- G,!,fail.
	neg(_).

% Data Request Processing
	requestData(I,ID,D,N,R,O) :- permit(I,ID,D,L), !, requestRecords(I,ID,D,L,M,N), get_time(T), stamp_date_time(T,Date,'UTC'), R = recordRequest(ID,I,ID,D,M,Date,true), L = [_,_,_,_,_,O,_|_].
	requestData(I,ID,D,N,R,O) :- get_time(T), stamp_date_time(T,Date,'UTC'), R = recordRequest(ID,I,ID,D,N,Date,false), O = [].

	requestRecords(I,ID,D,L,T,N) :- maxRecords(I,ID,D,L,M), M =:= -1, !, T = N.
	requestRecords(I,ID,D,L,T,N) :- maxRecords(I,ID,D,L,M), M >= N, T = N.
	requestRecords(I,ID,D,L,T,N) :- maxRecords(I,ID,D,L,M), M < N, T = M.
	maxRecords(I,ID,D,L,M) :- [_,_,_,C|_] = L, maxRecord(I,ID,C,D,M), M >= -1.
	maxRecord(I,ID,[recordsAccessed(CV)|_],D,M) :- !,findMaxRecord(I,ID,D,CV,M).
	maxRecord(I,ID,[recordsAccessed(ID,D,CV)|_],D,M) :- !,findMaxRecord(I,ID,D,CV,M).
	maxRecord(_,_,[_|_],_,M) :- M = -1.
	maxRecord(_,_,[],_,M) :- M = -1.
	findMaxRecord(I,ID,D,CV,M) :- numRecords(I,_,ID,D,R), functor(CV,C,_), C = <, arg(2,CV,V), M is V - R.
	findMaxRecord(I,ID,D,CV,M) :- numRecords(I,_,ID,D,R), functor(CV,C,_), C = =<, arg(2,CV,V), M is V - R.

% ==================== OLD CODE ====================
	/*% I is the owner, X is the requestor ID, Y is the data ID, L is the policy (usually a variable)
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'F'), checkIdentity(L,X), checkData(L,Y), checkConditions(I,L), checkPreObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'P'), checkIdentity(L,X), checkData(L,Y), checkConditions(I,L), checkPreObligations(L),!.
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'F'), checkIdentity(L,X), checkAnyData(I,L,Y), checkConditions(I,L), checkPreObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'P'), checkIdentity(L,X), checkAnyData(I,L,Y), checkConditions(I,L), checkPreObligations(L),!.

	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'F'), checkGroupIdentity(L,X), checkData(L,Y), checkConditions(I,L), checkPreObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'P'), checkGroupIdentity(L,X), checkData(L,Y), checkConditions(I,L), checkPreObligations(L),!.
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'F'), checkGroupIdentity(L,X), checkAnyData(I,L,Y), checkConditions(I,L), checkPreObligations(L),!,fail.
	permit(I,X,Y,L) :- policy(I,L), checkModality(L,'P'), checkGroupIdentity(L,X), checkAnyData(I,L,Y), checkConditions(I,L), checkPreObligations(L),!.

	permit(I,_,Y,L) :- policy(I,L), checkModality(L,'F'), checkAnyIdentity(L), checkData(L,Y), checkConditions(I,L), checkPreObligations(L),!,fail.
	permit(I,_,Y,L) :- policy(I,L), checkModality(L,'P'), checkAnyIdentity(L), checkData(L,Y), checkConditions(I,L), checkPreObligations(L),!.
	permit(I,_,Y,L) :- policy(I,L), checkModality(L,'F'), checkAnyIdentity(L), checkAnyData(I,L,Y), checkConditions(I,L), checkPreObligations(L),!,fail.
	permit(I,_,Y,L) :- policy(I,L), checkModality(L,'P'), checkAnyIdentity(L), checkAnyData(I,L,Y), checkConditions(I,L), checkPreObligations(L),!.

	% permit if no policies exist defaultPermit("T"). At this point, execution has stopped if there is an explicit permit or forbid, yes?
	permit(I,X,Y,L) :- defaultPermit(I,'T'), L= ['P',X,Y,[],[],[],false].*/

	%canRequest(ID,D,L) :- policy(ID,L), [_,self,_,_,_,_|_] = L

	%checkCondition([recordsAccessed(CV)|T],D) :- me(ID), conRecordsAccessed(ID,D,CV), checkCondition(T,D).
	%checkCondition([recordsAccessed(ID,D2,CV)|T],D) :- conRecordsAccessed(ID,D2,CV), checkCondition(T,D).
	%conRecordsAccessed(ID,D,C) :- integer(C), numRecords(ID,D,T), T < C.
	%checkCondition([recordsRequested(CV)|T],D) :- me(ID), conRecordsRequested(ID,D,CV), checkCondition(T,D).
	%checkCondition([recordsRequested(ID,D2,CV)|T],D) :- conRecordsRequested(ID,D2,CV), checkCondition(T,D).
	%checkCondition([requests(CV)|T],D) :- me(ID), conRequests(ID,D,CV), checkCondition(T,D).
	%checkCondition([requests(ID,D2,CV)|T],D) :- conRequests(ID,D2,CV), checkCondition(T,D).
	%checkCondition([lastRequest(CV)|T],D) :- me(ID), conLastRequest(ID,D,CV), checkCondition(T,D).
	%checkCondition([lastRequest(ID,D2,CV)|T],D) :- conLastRequest(ID,D2,CV), checkCondition(T,D).
	%conLastRequest(X,Y,V) :- group(X,_), !, findall(M,group(X,M),GMs), findall(T,recordRequest(GMs,Y,_,T,_),S), maxDate(S,M), date_time_stamp(V,VAc), M =< VAc, !, fail.
	%checkCondition([lastAccess(CV)|T],D) :- me(ID), conLastAccess(ID,D,CV), checkCondition(T,D).
	%checkCondition([_(lastAccess(ID,D2),V)|T],D) :- conLastAccess(ID,D2,CV), checkCondition(T,D).
	%checkCondition([year(CV)|T],D) :- conYear(CV), checkCondition(T,D).
	%checkCondition([month(CV)|T],D) :- conMonth(CV), checkCondition(T,D).
	%checkCondition([day(CV)|T],D) :- conDay(CV), checkCondition(T,D).
	%checkCondition([hour(CV)|T],D) :- conHour(CV), checkCondition(T,D).
	%checkCondition([minute(CV)|T],D) :- conMinute(CV), checkCondition(T,D).


	% Modality, Identity, and Data Checks
		% checkModality([M|_],X) :- =(M,X).
		% checkIdentitySingle(ID,X) :- =(ID,X).
		% checkIdentitySingle(ID,X) :- group(G,X), =(G,ID).
		% checkIdentitySingle(ID,_) :- =(ID,any).
		% checkIdentity([_,ID|_],X) :- =(ID,X).
		% checkGroupIdentity([_,ID|_],X) :- group(G,X), =(G,ID).
		% checkAnyIdentity([_,ID|_]) :- =(ID,any).
		% checkData([_,_,D|_],X) :- =(D,X).
		% checkAnyDataNoExist([_,_,D|_]) :- =(D,any).
		% checkAnyData(I,[_,_,D|_],X) :- =(D,any), hasData(I,I,X).

	% Obligation Checking
		/*checkPreObligations([_,_,_,_,O|_]) :- checkObligation(O).
		checkObligations([_,_,_,_,_,O|_]) :- checkObligation(O).
		:- discontiguous checkObligation/1.
			% obtain(Target,Data,Quantity)
				checkObligation([obtain(Tgt,D,Q)|T]) :- oblEnforce(Tgt,D,Q), checkObligation(T).
				oblObtain(_,_,_).

			% provide(Target,Data,Quantity,Peer)
				checkObligation([provide(Tgt,D,Q,ID)|T]) :- oblProvide(Tgt,D,Q,ID), checkObligation(T).
				oblProvide(_,_,_,_).

			% enforce(Target,Policy)
				checkObligation([enforce(Tgt,P)|T]) :- policy(P,_), oblEnforce(Tgt,P), checkObligation(T).
				oblEnforce(_,_).

			% inform(Target)
				checkObligation([inform(Tgt)|T]) :- oblInform(Tgt), checkObligation(T).
				oblInform(_).

		checkObligation([]).*/

		% listTrueConditions(L) :- policy(_,L), checkConditions(L).
		% listTruePreObligations(L) :- policy(_,L), checkPreObligations(L).
		% listTrueObligations(L) :- policy(_,L), checkObligations(L).

		% whatCanIAccess(Y) :- me(X), policy(_,L), permit(X,Y,L).
		% whoCanAccess(X,Y) :- peer(X), permit(X,Y,_).
		% whatCanTheyAccess(I,X,Y) :- data(Y,_,_,_), permit(I,X,Y,_).
		% whoCanAccessWhat(X,Y) :- peer(X), data(Y,_,_,_), permit(X,Y,_).

		%numRecords(I,X,Y,Z) :- findall(T,recordRequest(I,X,Y,T,_,true),S), listTotal(S,0,Z).
		%numData(I,D,Z) :- findall(T,dataElement(I,D,T),S), listTotal(S,0,Z).
		% greaterThanZero(X) :- ( X > 0 -> true;  false).

		/*recordObligations(L) :- [_,_,_,_,_,O|_] = L, recordObligation(O).
			recordObligation([provide(Tgt,D,Q,ID)|T]) :- write(obligation([Tgt,provide(D,Q,ID)])),nl, recordObligation(T).
			recordObligation([enforce(Tgt,P)|T]) :- policy(P,L), write(obligation([Tgt,enforce(L)])),nl, recordObligation(T).
		recordObligation([]).*/

		% requestData(I,ID,D,N,M) :- permit(I,ID,D,L), !, requestRecords(I,ID,D,L,M,N), get_time(T), stamp_date_time(T,Date,'UTC'), write(recordRequest(ID,ID,D,M,Date,true)), nl, recordObligations(L), sendRecords(D,M,M).
		% requestData(I,ID,D,N,R) :- permit(I,ID,D,L), !, requestRecords(I,ID,D,L,M,N), get_time(T), stamp_date_time(T,Date,'UTC'), R = recordRequest(ID,ID,D,M,Date,true), recordObligations(L).
		% sendRecords(D,M,T) :- M > 0, C is T - M + 1, write(C:[D,'-------------------']), nl, MNew is M - 1, sendRecords(D,MNew,T).
		% sendRecords(_,M,_) :- M =< 0.