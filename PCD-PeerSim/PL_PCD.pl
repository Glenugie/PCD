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
	%:- dynamic defaultPermit/2.

	% Policies: policy(PolicyOwner,Policy,Active?). -> policy(peer1,[POL],true).
	% [ 'P' , peer42 , d1 , [ [recordsAccessed(peer1,d1) < 50] , 0 , 0 ] , [] , [], false]
	% [ [ Active ], [ Deactive ], Mod, Src, Iss, Id, [ Action ], Rew, Pen]
	% [ [ requests(d2, peer1)=:=11 ], [ false ], 'F', peer9, peer9, peer2, [ dataAccess(d2, 10)], 5, 0 ]
	:- dynamic policy/3.

	% I should not request D from ID until cycle T
	% noRequest(FactOwner, ProviderID, Data, CycleToRevoke)
	:- dynamic noRequest/4.

% consult("c:/users/sam/dropbox/phd/pcd-peersim/pl_pcd"), consult("c:/users/sam/dropbox/phd/pcd-peersim/pl_pcd-javaoutput").

% Reference to the Java simulation's prolog intialisers (policies, peers, data, etc.)
%:- ['JavaOutput.pl'].

% TODO:
	% Update relRecords() to provide more filtering (currently quite broad)
	% Update checkCondition() (and subs) to reflect current condition list
	% Condition queries (numRecords, numRequests, etc.)

% Policy Activation/Deactivation
	processPolicies(I) :- processPolicy(I).
	processPolicy(I) :- policy(I,L,true), [_,C,_,_,_,_,_,_,_],  checkConditions(I,C,true), !, retract(policy(I,L,true)), assert(policy(I,L,false)).
	processPolicy(I) :- policy(I,L,false), [C,_,_,_,_,_,_,_,_], checkConditions(I,C,false), !, retract(policy(I,L,false)), assert(policy(I,L,true)).

	deactiveAllSingleCycle() :- policy(I, [_,C,_,_,_,_,_,_,_], true), member(true,C), retract(policy(I,L,true)), assert(policy(I,L,false)).

	% Condition Checking
		checkConditions(I,C,A) :- checkCondition(I,C,A).
			:- discontiguous checkCondition/3.
			% Boolean
				checkCondition(I,[true|T],A) :- !, checkCondition(I,T,A).
				checkCondition(_,[false|_],_) :- !, fail.

			% Records Accessed: recordsAccessed(Peer,Data) Operator Num
				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,recordsAccessed(ID,D2)), arg(2,H,V), conRecordsAccessed(I,ID,D2,Comp,V), checkCondition(I,T,A).
				conRecordsAccessed(I,ID,D,C,V) :- numRecords(I,_,ID,D,T), conditionComparator(T,V,C).

			% Records Requested: recordsRequested(Peer,Data) Operator Num
				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,recordsRequested(ID,D2)), arg(2,H,V), conRecordsRequested(I,ID,D2,Comp,V), checkCondition(I,T,A).
				conRecordsRequested(I,ID,D,C,V) :- numRecordsRequested(I,_,ID,D,T), conditionComparator(T,V,C).

			% Requests: requests(Peer,Data) Operator Num
				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,requests(ID,D2)), arg(2,H,V), conRequests(I,ID,D2,Comp,V), checkCondition(I,T,A).
				conRequests(I,ID,D,C,V) :- numRequests(I,_,ID,D,T), conditionComparator(T,V,C).

			% Last Request lastRequest(Peer,Data) Operator Date
				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,lastRequest(ID,D2)), arg(2,H,V), conLastRequest(I,ID,D2,Comp,V), checkCondition(I,T,A).
				conLastRequest(I,ID,D,C,V) :- findall(T,recordRequest(I,_,ID,D,_,T,_),S), maxDate(S,M), date_time_stamp(V,VAc), conditionComparator(M,VAc,C).

			% Last Access lastAccess(Peer,Data) Operator Date
				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,lastAccess(ID,D2)), arg(2,H,V), conLastAccess(I,ID,D2,Comp,V), checkCondition(I,T,A).
				conLastAccess(I,ID,D,C,V) :- findall(T,recordRequest(I,_,ID,D,_,T,true),S), maxDate(S,M), date_time_stamp(V,VAc), conditionComparator(M,VAc,C).

			% Date/Time: year/month/day/hour/minute() Operator Num
				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,year()), arg(2,H,Y), conYear(Comp,Y), checkCondition(I,T,A).
				conYear(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('year',D,VC), conditionComparator(V,VC,C).

				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,month()), arg(2,H,M), conMonth(Comp,M), checkCondition(I,T,A).
				conMonth(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('month',D,VC), conditionComparator(V,VC,C).

				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,day()), arg(2,H,Da), conDay(Comp,Da), checkCondition(I,T,A).
				conDay(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('day',D,VC), conditionComparator(V,VC,C).

				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,hour()), arg(2,H,Ho), conHour(Comp,Ho), checkCondition(I,T,A).
				conHour(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('hour',D,VC), conditionComparator(V,VC,C).

				checkCondition(I,[H|T],A) :- functor(H,Comp,_), arg(1,H,minute()), arg(2,H,M), conMinute(Comp,M), checkCondition(I,T,A).
				conMinute(C,V) :- get_time(T), stamp_date_time(T,D,'UTC'), date_time_value('minute',D,VC), conditionComparator(V,VC,C).

		checkCondition(_,_,_).
		checkCondition(_,[],_).

	% What conditions are inactive. Takes a policy set, and returns a list of inactive activation coniditions, and active deactivation conditions
		inactiveConditions(L,C).

% Permission Checkers
	% F Peer Data, 	P Peer Data, 	F Peer any, 	P Peer any
	% F Group Data, P Group Data, 	F Group any, 	P Group any
	% F any Data, 	P any Data, 	F any any, 		P any any

% Checks if I's current (self-targetting) policies prevent requesting D from ID, and returns matching policy(s)
	%% permitRequest(I,ID,D,_) :- noRequest(I,ID,D,_),!,fail.
	%% permitRequest(I,ID,D,L) :- policy(I,L), L=['F',ID,D,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	%% permitRequest(I,ID,D,L) :- policy(I,L), L=['P',ID,D,_,_,_,true,_,_], checkConditions(I,L),!.
	%% permitRequest(I,ID,_,L) :- policy(I,L), L=['F',ID,any,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	%% permitRequest(I,ID,_,L) :- policy(I,L), L=['P',ID,any,_,_,_,true,_,_], checkConditions(I,L),!.
	%% permitRequest(I,ID,D,L) :- policy(I,L), L=['F',G,D,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!,fail.
	%% permitRequest(I,ID,D,L) :- policy(I,L), L=['P',G,D,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!.
	%% permitRequest(I,ID,_,L) :- policy(I,L), L=['F',G,any,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!,fail.
	%% permitRequest(I,ID,_,L) :- policy(I,L), L=['P',G,any,_,_,_,true,_,_], group(G,ID), checkConditions(I,L),!.
	%% permitRequest(I,_,D,L) :- policy(I,L), L=['F',any,D,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	%% permitRequest(I,_,D,L) :- policy(I,L), L=['P',any,D,_,_,_,true,_,_], checkConditions(I,L),!.
	%% permitRequest(I,_,_,L) :- policy(I,L), L=['F',any,any,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	%% permitRequest(I,_,_,L) :- policy(I,L), L=['P',any,any,_,_,_,true,_,_], checkConditions(I,L),!.
	%% permitRequest(_,ID,D,L) :- L= ['P',ID,D,[],[],[],true,_,_].

% Checks if a specific (self-targetting) policy (L) prevents I from requesting D from anyone
	%% permitRequestPolicy(I,D,L) :- L=['F',_,D,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	%% permitRequestPolicy(I,D,L) :- L=['P',_,D,_,_,_,true,_,_], checkConditions(I,L),!.
	%% permitRequestPolicy(I,_,L) :- L=['F',_,any,_,_,_,true,_,_], checkConditions(I,L),!,fail.
	%% permitRequestPolicy(I,_,L) :- L=['P',_,any,_,_,_,true,_,_], checkConditions(I,L),!.
	%% permitRequestPolicy(_,_,_).

% Checks if I's current policies prevent providing D to ID, and returns matching policy(s)
	%% permit(I,ID,D,L) :- policy(I,L), L=['F',ID,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	%% permit(I,ID,D,L) :- policy(I,L), L=['P',ID,D,_,_,_,false,_,_], checkConditions(I,L),!.
	%% permit(I,ID,D,L) :- policy(I,L), L=['F',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	%% permit(I,ID,D,L) :- policy(I,L), L=['P',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	%% permit(I,ID,D,L) :- policy(I,L), L=['F',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!,fail.
	%% permit(I,ID,D,L) :- policy(I,L), L=['P',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!.
	%% permit(I,ID,D,L) :- policy(I,L), L=['F',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!,fail.
	%% permit(I,ID,D,L) :- policy(I,L), L=['P',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!.
	%% permit(I,_,D,L) :- policy(I,L), L=['F',any,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	%% permit(I,_,D,L) :- policy(I,L), L=['P',any,D,_,_,_,false,_,_], checkConditions(I,L),!.
	%% permit(I,_,D,L) :- policy(I,L), L=['F',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	%% permit(I,_,D,L) :- policy(I,L), L=['P',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	%% permit(I,ID,D,L) :- defaultPermit(I,'T'), L= ['P',ID,D,[],[],[],false,_,_].

% Checks if a specific policy (L) forbids I from providing D to ID
	%% permitPolicy(I,ID,D,L) :- L=['F',ID,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	%% permitPolicy(I,ID,D,L) :- L=['P',ID,D,_,_,_,false,_,_], checkConditions(I,L),!.
	%% permitPolicy(I,ID,D,L) :- L=['F',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	%% permitPolicy(I,ID,D,L) :- L=['P',ID,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	%% permitPolicy(I,ID,D,L) :- L=['F',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!,fail.
	%% permitPolicy(I,ID,D,L) :- L=['P',G,D,_,_,_,false,_,_], group(G,ID), checkConditions(I,L),!.
	%% permitPolicy(I,ID,D,L) :- L=['F',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!,fail.
	%% permitPolicy(I,ID,D,L) :- L=['P',G,any,_,_,_,false,_,_], group(G,ID), hasData(I,I,D), checkConditions(I,L),!.
	%% permitPolicy(I,_,D,L) :- L=['F',any,D,_,_,_,false,_,_], checkConditions(I,L),!,fail.
	%% permitPolicy(I,_,D,L) :- L=['P',any,D,_,_,_,false,_,_], checkConditions(I,L),!.
	%% permitPolicy(I,_,D,L) :- L=['F',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!,fail.
	%% permitPolicy(I,_,D,L) :- L=['P',any,any,_,_,_,false,_,_], hasData(I,I,D), checkConditions(I,L),!.
	%% permitPolicy(_,_,_,_).

% [NEW] Checks if at least one permit policy in a set is active
	polIsActive(P,[AC,DC,M,Src,Iss,R,Act,Rew,Pen]) :- policy(P,[AC,DC,M,Src,Iss,R,Act,Rew,Pen],true).
	polIsActive(P,[AC,DC,M,Src,Iss,R,Act,Rew,Pen]) :- neg(policy(P,[AC,DC,M,Src,Iss,R,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)).

	permit([[AC,DC,'P',Src,Iss,R,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(D,_),Act), policy(P,[AC,DC,'P',Src,Iss,R,Act,Rew,Pen],true), !.
	permit([[AC,DC,'P',Src,Iss,R,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(D,_),Act), neg(policy(P,[AC,DC,'P',Src,Iss,R,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)), !.
	permit([[AC,DC,'P',Src,Iss,R,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(any,_),Act), policy(P,[AC,DC,'P',Src,Iss,R,Act,Rew,Pen],true), !.
	permit([[AC,DC,'P',Src,Iss,R,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(any,_),Act), neg(policy(P,[AC,DC,'P',Src,Iss,R,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)), !.

	permit([[AC,DC,'P',Src,Iss,G,Act,Rew,Pen]|T],P,R,D) :- group(G,R), member(dataAccess(D,_),Act), policy(P,[AC,DC,'P',Src,Iss,G,Act,Rew,Pen],true), !.
	permit([[AC,DC,'P',Src,Iss,G,Act,Rew,Pen]|T],P,R,D) :- group(G,R), member(dataAccess(D,_),Act), neg(policy(P,[AC,DC,'P',Src,Iss,G,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)), !.
	permit([[AC,DC,'P',Src,Iss,G,Act,Rew,Pen]|T],P,R,D) :- group(G,R), member(dataAccess(any,_),Act), policy(P,[AC,DC,'P',Src,Iss,G,Act,Rew,Pen],true), !.
	permit([[AC,DC,'P',Src,Iss,G,Act,Rew,Pen]|T],P,R,D) :- group(G,R), member(dataAccess(any,_),Act), neg(policy(P,[AC,DC,'P',Src,Iss,G,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)), !.

	permit([[AC,DC,'P',Src,Iss,any,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(D,_),Act), policy(P,[AC,DC,'P',Src,Iss,any,Act,Rew,Pen],true), !.
	permit([[AC,DC,'P',Src,Iss,any,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(D,_),Act), neg(policy(P,[AC,DC,'P',Src,Iss,any,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)), !.
	permit([[AC,DC,'P',Src,Iss,any,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(any,_),Act), policy(P,[AC,DC,'P',Src,Iss,any,Act,Rew,Pen],true), !.
	permit([[AC,DC,'P',Src,Iss,any,Act,Rew,Pen]|T],P,R,D) :- member(dataAccess(any,_),Act), neg(policy(P,[AC,DC,'P',Src,Iss,any,Act,Rew,Pen],false)), checkConditions(I,AC,false), neg(checkConditions(I,DC,true)), !.

	permit([[_,_,_,_,_,_,_,_,_]|T],P,R,D) :- permit(T,P,R,D).
	permit([],_) :- !,fail.

% Gathers List of Relevant Policies
	% [ [ Active ], [ Deactive ], Mod, Src, Iss, Id, [ Action ], Rew, Pen]
	relPolicies(I,ID,D,L) :- policy(I,L,_), L=[_,_,_,_,_,ID,Act,_,_], member(dataAccess(D,_),Act).
	relPolicies(I,ID,_,L) :- policy(I,L,_), L=[_,_,_,_,_,ID,Act,_,_], member(dataAccess(any,_),Act).
	relPolicies(I,ID,D,L) :- policy(I,L,_), L=[_,_,_,_,_,G,Act,_,_], member(dataAccess(D,_),Act), group(G,ID).
	relPolicies(I,ID,_,L) :- policy(I,L,_), L=[_,_,_,_,_,G,Act,_,_], member(dataAccess(any,_),Act), group(G,ID).
	relPolicies(I,_,D,L) :- policy(I,L,_), L=[_,_,_,_,_,any,Act,_,_], member(dataAccess(D,_),Act).
	relPolicies(I,_,_,L) :- policy(I,L,_), L=[_,_,_,_,_,any,Act,_,_], member(dataAccess(any,_),Act).

% [NEW] Cost of sending a Data_Request
	relReqPolicies(I,ID,D,L) :- policy(I,L,true), L=[_,_,_,_,_,ID,Act,_,_], member(dataAccess(D,_),Act).
	relReqPolicies(I,ID,_,L) :- policy(I,L,true), L=[_,_,_,_,_,ID,Act,_,_], member(dataAccess(any,_),Act).

% Checks if a policy is relevant (to be called on receipt of relPolicies, this is used by the requestor to filter out irrelevant policies based on their records)
	% policyRelevant(I,L) :- checkConditions(I,L).

% Gathers List of Relevant Records
	% Need to capture data and peers mentioned in conditions/obligations
	%relRecords(I,X,[M,ID,D,C,O],R) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D,Q,T,G), R = recordRequest(I,I,D,Q,T,G).
	%relRecords(I,X,[M,_,RID,D,C,PO,O],R) :- policy(X,[M,RID,D,C,PO,O,_,_,_]), recordRequest(I,X,I,D2,Q,T,G), R = recordRequest(I,X,I,D2,Q,T,G). % Reports ALL records as relevant
	%relRecords(I,X,[M,ID,D,C,O],PID) :- policy(X,_,[M,ID,D,C,O]), PID = policy(X,_,[M,ID,D,C,O]).
	%relRecords(I,X,[M,ID,D,C,O],ID2,D2,Q,T,G) :- policy(X,_,[M,ID,D,C,O]), recordRequest(I,I,D,Q,T,G), ID2 = ID, D2 = D.

	% recordRequest(RecordOwner, ProviderID, RequestorID, DataID, Quantity, Date, RequestApproved)
	% relRecords(Identity, Policy)
	relRecords(I,P,R) :- TR = recordRequest(I,I,Req,D,Q,Date,Acc), relRecord(P,TR), R = recordRequest(I,I,Req,D,Q,Date,Acc).
	relRecords(I,P,R) :- TR = recordRequest(I,Prv,I,D,Q,Date,Acc), relRecord(P,TR), R = recordRequest(I,Prv,I,D,Q,Date,Acc).
		% Records with the same Data as is being requested
		relRecord([[_,_,_,_,_,_,Act,_,_]|_],TR) :- arg(4,TR,D), member(dataAccess(D,_),Act),!.

		% Records with the same Data/Identity as any recordsAccessed/recordsRequested/requests/lastRequest/lastAccess conditions
		relRecord([[_,_,_,_,_,_,_,_,_]|_],_) :- !. % Forces the return of all records

		relRecord([[_,_,_,_,_,_,_,_,_]|T],TR) :- relRecord(T,TR).
		relRecord([],_) :- !,fail.



%  polsViolatedBy(I, Pol, L): Returns policies (self or otherwise) held by I violated by Pol
%% polsViolatedBy(I,Pol,L) :- policy(I,L), polViolates(I,Pol,L).

%% % polsViolates(I, Pol1, Pol2): Returns true if Pol2 conflicts with Pol1
%% polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,any,_,_,_,S1,_,_], Pol2=[_,ID2,_,_,_,_,S2,_,_], S1=S2, ID1=ID2. 							% ID: Any=Any,Group=Group,Peer=Peer. Data: Any=Any,Any=Data
%% polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,D1,_,_,_,S1,_,_], Pol2=[_,ID2,D2,_,_,_,S2,_,_], S1=S2, ID1=ID2, D1=D2. 						% ID: Any=Any,Group=Group,Peer=Peer. Data: Any=Any,Data=Data
%% polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,any,_,_,_,S1,_,_], Pol2=[_,_,_,_,_,_,S2,_,_], S1=S2, group(ID1,_). 							% ID: Group=Peer. Data: Any=Any,Any=Data
%% polViolates(_,Pol1,Pol2) :- Pol1=[_,ID1,D1,_,_,_,S1,_,_], Pol2=[_,_,D2,_,_,_,S2,_,_], S1=S2, group(ID1,_), D1=D2. 					% ID: Group=Peer. Data: Any=Any,Data=Data
%% %polViolates(I,Pol1,Pol2) :- Pol1=[_,ID1,any,_,_,_,S1,_,_], Pol2=[_,ID2,_,_,_,_,S2,_,_], S1=S2, group(ID1,I), group(ID2,I). 		% ID: Shared Group Data: Any=Any,Any=Data
%% %polViolates(I,Pol1,Pol2) :- Pol1=[_,ID1,D1,_,_,_,S1,_,_], Pol2=[_,ID2,D2,_,_,_,S2,_,_], S1=S2, group(ID1,I), group(ID2,I), D1=D2. 	% ID: Shared Group Data: Any=Any,Data=Data
%% polViolates(_,Pol1,Pol2) :- Pol1=[_,any,any,_,_,_,S1,_,_], Pol2=[_,_,_,_,_,_,S2,_,_], S1=S2. 										% ID: Any=Any,Any=Group,Any=Peer. Data: Any=Any,Any=Data
%% polViolates(_,Pol1,Pol2) :- Pol1=[_,any,D1,_,_,_,S1,_,_], Pol2=[_,_,D2,_,_,_,S2,_,_], S1=S2, D1=D2. 								% ID: Any=Any,Any=Group,Any=Peer. Data: Any=Any,Data=Data

% Finding Confirmed or Possible Data
	% findData :- connected/2 entries which do not have noData/3 entries
	findData(I,D,L) :- hasData(I,_,D),!,findall(ID,hasData(I,ID,D),L).
	findData(I,D,L) :- findall(ID,possibleData(I,ID,D),L).
	possibleData(I,ID,D) :- connected(I,ID), neg(noData(I,ID,D)).
	numData(I,D,Z) :- findall(T,dataElement(I,D,T),S), listLength(S,0,Z).

	numWithoutData(I,D,Z) :- findall(ID,neighbourWithoutData(I,ID,D),S), listLength(S,0,Z). 
	neighbourWithoutData(I,ID,D) :- connected(I,ID), noData(I,ID,D).

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

	% Checks if X is a member of the list
	member(X,[X|_]). 
   	member(X,[_|T]) :- member(X,T).

   	dataElement(peer0,d1,00000).
   	dataElement(peer0,d1,00001).
   	dataElement(peer0,d1,00002).
   	dataElement(peer0,d1,00003).
   	dataElement(peer0,d2,00000).

   	countData(ID,D,N) :- countData(ID,D,[], 0, N).
	countData(ID, D, Seen, Acc, N) :-
	    dataElement(ID,D,X),
	    (   member(X, Seen)
	    ->  fail
	    ;   !,
	        Acc1 is Acc + 1,
	        countData(ID, D, [X|Seen], Acc1, N)
	    ).
	countData(_, _, _, N, N).


% [New] requestData method to return: the number of elements to provide, appropriate transaction records, reward to provide, penalty cycles to issue
	% requestData( Provider, Requestor, PolicySet, Data, Quantity, Records, Reward, Penalty)
	% requestData(peer0, peer1, [[[],[],'P',1,1,1,[],5,10],[[],[],'F',1,1,1,[],1,7]], d1, 5, R, Rew, Pen).
	requestData(Prv,Req,L,D,N,R,Rew,Pen) :- permit(L,Prv,Req,D), requestElements(Prv,D,L,M,N), M > 0, !, polTotalRP(Prv,L,0,Rew,0,Pen), get_time(T), stamp_date_time(T,Date,'UTC'), assert(recordRequest(Prv,Prv,Req,D,M,Date,true)), R = recordRequest(Req,Prv,Req,D,M,Date,true).
	requestData(Prv,Req,_,D,N,R,Rew,Pen) :- Rew = 0, Pen = 0, get_time(T), stamp_date_time(T,Date,'UTC'), assert(recordRequest(Prv,Prv,Req,D,N,Date,false)), R = recordRequest(Req,Prv,Req,D,N,Date,false).

	% polTotalRP(1,[[[],[],'P',1,1,1,[],5,10],[[],[],'F',1,1,1,[],1,7]],0,R,0,P).
	polTotalRP(Prv,[[AC,DC,'P',Src,Iss,ID,Act,Rew,Pen]|T],R,RF,P,PF) :- polIsActive(Prv,[AC,DC,'P',Src,Iss,ID,Act,Rew,Pen]), !, RNew is R+Rew, polTotalRP(Prv,T,RNew,RF,P,PF).
	polTotalRP(Prv,[[AC,DC,'F',Src,Iss,ID,Act,Rew,Pen]|T],R,RF,P,PF) :- polIsActive(Prv,[AC,DC,'F',Src,Iss,ID,Act,Rew,Pen]), !, PNew is P+Pen, polTotalRP(Prv,T,R,RF,PNew,PF).
	polTotalRP(Prv,[_|T],R,RF,P,PF) :- polTotalRP(Prv,T,R,RF,P,PF).
	polTotalRP(_,[],R,R,P,P).

	% Processes the result of polMaxElements, to determine how much of the available R wants
	%requestElements(Prv,D,L,T,N) :- polMaxElements(Prv,D,L,-1,M), M =:= -1, defaultPermit(Prv,'T'), countData(Prv,D,OD), N > OD, !, T = OD.
	%requestElements(Prv,D,L,T,N) :- polMaxElements(Prv,D,L,-1,M), M =:= -1, defaultPermit(Prv,'T'), !, T = N.
	requestElements(Prv,D,L,T,_) :- polMaxElements(Prv,D,L,-1,M), M =:= -1, !, T = 0.
	requestElements(Prv,D,L,T,N) :- polMaxElements(Prv,D,L,-1,M), M >= N, countData(Prv,D,OD), N > OD, T = OD.
	requestElements(Prv,D,L,T,N) :- polMaxElements(Prv,D,L,-1,M), M >= N, T = N.
	requestElements(Prv,D,L,T,N) :- polMaxElements(Prv,D,L,-1,M), M < N, countData(Prv,D,OD), M > OD, T = OD.
	requestElements(Prv,D,L,T,N) :- polMaxElements(Prv,D,L,-1,M), M < N, T = M.

	% Finds the maximum possible number of elements to provide
	polMaxElements(Prv,D,[[AC,DC,'P',Src,Iss,ID,Act,Rew,Pen]|T],N,M) :- polIsActive(Prv,[AC,DC,'P',Src,Iss,ID,Act,Rew,Pen]), member(dataAccess(D,Q),Act), N =:= -1, !, NNew is Q, polMaxElements(Prv,T,NNew,M).
	polMaxElements(Prv,D,[[AC,DC,'P',Src,Iss,ID,Act,Rew,Pen]|T],N,M) :- polIsActive(Prv,[AC,DC,'P',Src,Iss,ID,Act,Rew,Pen]), member(dataAccess(D,Q),Act), N > Q, !, NNew is Q, polMaxElements(Prv,T,NNew,M).
	polMaxElements(Prv,[_|T],N,M) :- polMaxElements(Prv,T,N,M).
	polmaxElements(_,[],N,N).

	generateDatalessPackage(Prv,Req,D,N,R) :- get_time(T), stamp_date_time(T,Date,'UTC'), assert(recordRequest(Prv,Prv,Req,D,N,Date,true)), R = recordRequest(Req,Prv,Req,D,N,Date,true).

% Data Request Processing
	%% requestData(I,ID,D,N,R,O) :- permit(I,ID,D,L), !, requestRecords(P,R,D,L,M,N), get_time(T), stamp_date_time(T,Date,'UTC'), R = recordRequest(ID,I,ID,D,M,Date,true), L = [_,_,_,_,_,O,_|_].
	%% requestData(I,ID,D,N,R,O) :- get_time(T), stamp_date_time(T,Date,'UTC'), R = recordRequest(ID,I,ID,D,N,Date,false), O = [].

	%% requestRecords(P,R,D,L,T,N) :- maxRecords(I,ID,D,L,M), M =:= -1, !, T = N.
	%% requestRecords(P,R,D,L,T,N) :- maxRecords(I,ID,D,L,M), M >= N, T = N.
	%% requestRecords(P,R,D,L,T,N) :- maxRecords(I,ID,D,L,M), M < N, T = M.
	%% maxRecords(I,ID,D,L,M) :- [_,_,_,C|_] = L, maxRecord(I,ID,C,D,M), M >= -1.
	%% maxRecord(I,ID,[recordsAccessed(CV)|_],D,M) :- !,findMaxRecord(I,ID,D,CV,M).
	%% maxRecord(I,ID,[recordsAccessed(ID,D,CV)|_],D,M) :- !,findMaxRecord(I,ID,D,CV,M).
	%% maxRecord(_,_,[_|_],_,M) :- M = -1.
	%% maxRecord(_,_,[],_,M) :- M = -1.
	%% findMaxRecord(I,ID,D,CV,M) :- numRecords(I,_,ID,D,R), functor(CV,C,_), C = <, arg(2,CV,V), M is V - R.
	%% findMaxRecord(I,ID,D,CV,M) :- numRecords(I,_,ID,D,R), functor(CV,C,_), C = =<, arg(2,CV,V), M is V - R.