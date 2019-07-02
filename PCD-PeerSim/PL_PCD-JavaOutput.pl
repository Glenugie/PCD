:- dynamic data/4.

data(d1, d0, d0, '000000').
data(d2, d0, d0, '000000').
data(d3, d0, d0, '000000').
data(d4, d0, d0, '000000').
data(d5, d0, d0, '000000').

:- dynamic peer/1.

peer(peer0).
peer(peer1).
peer(peer2).
peer(peer3).
peer(peer4).
peer(peer5).
peer(peer6).
peer(peer7).
peer(peer8).
peer(peer9).

:- dynamic hasData/3.

hasData(peer0, peer0, d4).
hasData(peer0, peer0, d2).
hasData(peer0, peer0, d3).
hasData(peer0, peer0, d1).
hasData(peer1, peer1, d2).
hasData(peer1, peer1, d4).
hasData(peer2, peer2, d3).
hasData(peer3, peer3, d3).
hasData(peer3, peer3, d4).
hasData(peer3, peer3, d2).
hasData(peer4, peer4, d2).
hasData(peer4, peer4, d4).
hasData(peer5, peer5, d2).
hasData(peer5, peer5, d1).
hasData(peer6, peer6, d3).
hasData(peer6, peer6, d4).
hasData(peer6, peer6, d1).
hasData(peer7, peer7, d1).
hasData(peer8, peer8, d3).
hasData(peer8, peer8, d2).
hasData(peer9, peer9, d3).
hasData(peer9, peer9, d2).

:- dynamic group/2.

group(g141, peer1).
group(g661, peer2).

:- dynamic policy/2.

policy(peer0, ['P', any, d2, [], [], [], false]).
policy(peer0, ['P', peer7, d4, [], [], [], false]).
policy(peer0, ['P', peer3, d4, [], [], [], false]).
policy(peer0, ['P', peer5, d4, [], [], [], false]).
policy(peer0, ['F', peer6, d2, [], [], [], false]).
policy(peer0, ['F', peer8, d2, [], [], [], false]).
policy(peer0, ['P', peer6, d1, [], [], [], false]).
policy(peer0, ['P', peer2, d5, [], [], [], false]).
policy(peer0, ['P', peer6, d4, [], [], [], false]).
policy(peer0, ['P', peer4, d3, [], [], [], false]).
policy(peer0, ['P', peer7, d5, [], [], [], false]).
policy(peer0, ['P', peer9, d4, [], [], [], false]).
policy(peer0, ['P', peer9, d5, [], [provide(d3, 2, peer7)], [[[provide(d3, 2, peer0)], 10, 6], [[inform(peer0)], 20, 7]], false]).
policy(peer0, ['P', peer3, d5, [], [], [[[obtain(d3, 8)], 20, 8]], false]).
policy(peer0, ['P', peer1, d5, [], [], [], false]).
policy(peer0, ['P', peer4, d2, [], [], [], false]).
policy(peer0, ['P', peer2, d2, [], [], [], false]).
policy(peer0, ['P', peer8, d3, [], [], [], false]).
policy(peer0, ['P', peer5, d2, [], [], [], false]).
policy(peer0, ['P', peer3, d3, [], [], [], false]).
policy(peer0, ['P', peer8, d1, [], [], [], false]).
policy(peer0, ['F', peer1, d2, [], [], [], false]).
policy(peer0, ['P', peer4, d4, [], [], [], false]).
policy(peer0, ['P', peer6, d5, [], [], [], false]).
policy(peer0, ['P', peer3, d1, [], [], [], false]).
policy(peer0, ['F', peer2, d3, [], [], [], false]).
policy(peer0, ['P', peer4, d1, [], [], [], false]).
policy(peer0, ['P', peer4, d5, [], [], [], false]).
policy(peer0, ['F', peer8, d4, [], [], [], false]).
policy(peer0, ['P', peer8, d5, [], [], [], false]).
policy(peer0, ['F', peer5, d5, [], [], [], false]).
policy(peer0, ['P', peer2, d4, [], [], [], false]).
policy(peer0, ['P', peer7, d3, [], [], [], false]).
policy(peer0, ['P', peer9, d3, [], [], [], false]).
policy(peer0, ['P', peer1, d1, [], [], [], false]).
policy(peer0, ['P', peer2, d1, [], [], [], false]).
policy(peer0, ['P', peer6, d3, [], [], [], false]).
policy(peer0, ['P', peer7, d2, [], [], [], false]).
policy(peer0, ['P', peer7, d1, [], [], [], false]).
policy(peer0, ['F', peer9, d1, [], [], [], false]).
policy(peer0, ['F', peer1, d4, [], [], [], false]).
policy(peer0, ['P', peer5, d1, [], [], [], false]).
policy(peer0, ['P', peer1, d3, [], [], [], false]).
policy(peer0, ['P', peer9, d2, [], [], [], false]).
policy(peer0, ['P', any, d3, [], [], [], false]).
policy(peer1, ['P', peer2, d4, [], [], [], false]).
policy(peer1, ['P', peer2, d5, [], [], [], false]).
policy(peer1, ['P', peer7, d1, [], [], [], false]).
policy(peer1, ['P', peer4, d4, [], [], [], false]).
policy(peer1, ['F', peer8, d1, [], [], [], false]).
policy(peer1, ['F', peer9, d2, [requests(d2, peer1)=:=11], [], [], false]).
policy(peer1, ['P', peer3, d3, [], [], [], false]).
policy(peer1, ['F', peer5, d5, [], [], [], false]).
policy(peer1, ['P', peer9, d1, [], [], [], false]).
policy(peer1, ['P', peer4, d2, [], [], [], false]).
policy(peer1, ['P', peer3, d2, [], [], [], false]).
policy(peer1, ['P', peer7, d5, [], [], [[[obtain(d2, 1)], 5, 9], [[obtain(d4, 10)], 10, 8], [[inform(peer1)], 10, 6]], false]).
policy(peer1, ['P', peer9, d3, [], [], [], false]).
policy(peer1, ['P', peer9, d4, [], [], [], false]).
policy(peer1, ['P', peer5, d2, [], [], [], false]).
policy(peer1, ['P', any, d4, [], [], [], false]).
policy(peer1, ['P', peer7, d3, [], [], [], false]).
policy(peer1, ['P', peer0, d5, [lastRequest(d4, peer7)=\=63, lastAccess(d2, peer5)=<54], [], [], false]).
policy(peer1, ['P', peer3, d5, [], [], [], false]).
policy(peer1, ['P', peer5, d4, [], [], [], false]).
policy(peer1, ['F', peer4, d5, [], [], [], false]).
policy(peer1, ['P', peer6, d3, [], [], [], false]).
policy(peer1, ['P', peer7, d2, [], [], [], false]).
policy(peer1, ['F', peer9, d5, [lastRequest(d3, peer4)=\=86], [], [], false]).
policy(peer1, ['P', peer2, d2, [], [], [], false]).
policy(peer1, ['P', any, d1, [], [], [], false]).
policy(peer1, ['P', peer4, d3, [], [], [], false]).
policy(peer1, ['P', peer0, d3, [], [], [], false]).
policy(peer1, ['P', peer3, d1, [], [], [], false]).
policy(peer1, ['P', peer5, d3, [], [], [], false]).
policy(peer1, ['P', peer5, d1, [], [], [], false]).
policy(peer1, ['P', peer0, d4, [recordsAccessed(d3, peer1)<16, recordsAccessed(d3, peer8)=:=70], [], [], false]).
policy(peer1, ['P', peer3, d4, [], [], [], false]).
policy(peer1, ['P', peer6, d5, [], [], [], false]).
policy(peer1, ['P', peer0, d1, [recordsAccessed(d4, peer7)<52, lastRequest(d1, peer7)>=60], [], [], false]).
policy(peer1, ['P', peer2, d3, [], [], [], false]).
policy(peer1, ['P', peer2, d1, [], [], [], false]).
policy(peer1, ['P', peer8, d4, [], [], [], false]).
policy(peer1, ['P', peer0, d2, [], [], [], false]).
policy(peer1, ['P', peer6, d2, [], [], [], false]).
policy(peer1, ['P', peer8, d2, [], [], [], false]).
policy(peer1, ['P', peer6, d4, [], [], [], false]).
policy(peer1, ['P', peer8, d3, [], [], [], false]).
policy(peer1, ['P', peer8, d5, [], [], [], false]).
policy(peer1, ['P', peer6, d1, [], [], [], false]).
policy(peer2, ['F', peer7, d4, [], [], [], false]).
policy(peer2, ['P', peer5, d4, [], [], [], false]).
policy(peer2, ['F', peer5, d2, [], [], [], false]).
policy(peer2, ['P', peer3, d5, [], [], [], false]).
policy(peer2, ['F', peer5, d1, [], [obtain(d2, 4)], [[[provide(d1, 1, peer0)], 5, 7]], false]).
policy(peer2, ['P', peer3, d2, [], [], [], false]).
policy(peer2, ['P', peer0, d4, [], [], [], false]).
policy(peer2, ['P', peer5, d3, [], [], [], false]).
policy(peer2, ['P', peer4, d5, [], [], [], false]).
policy(peer2, ['F', peer6, d2, [], [], [], false]).
policy(peer2, ['P', peer3, d4, [], [], [], false]).
policy(peer2, ['F', peer6, d4, [], [], [], false]).
policy(peer2, ['P', peer8, d2, [], [], [], false]).
policy(peer2, ['P', peer0, d1, [], [], [], false]).
policy(peer2, ['P', peer7, d5, [], [], [], false]).
policy(peer2, ['F', peer0, d3, [], [], [], false]).
policy(peer2, ['P', peer9, any, [], [], [[[obtain(d4, 5)], 5, 6], [[adopt(['F', peer2, d2, [], [], [], false], 4)], 20, 8], [[adopt(['P', peer2, d2, [], [], [], true], 1)], 25, 9]], false]).
policy(peer2, ['P', peer8, d5, [], [], [], false]).
policy(peer2, ['P', any, d3, [], [], [], false]).
policy(peer2, ['P', peer0, d5, [], [], [], false]).
policy(peer2, ['P', peer4, d3, [], [], [], false]).
policy(peer2, ['P', peer4, d4, [], [], [], false]).
policy(peer2, ['P', peer4, d2, [], [], [], false]).
policy(peer2, ['P', peer6, d1, [], [], [], false]).
policy(peer2, ['P', peer4, d1, [], [], [], false]).
policy(peer2, ['P', peer0, d2, [], [], [], false]).
policy(peer3, ['P', peer8, d3, [], [], [], false]).
policy(peer3, ['P', peer7, d1, [], [], [], false]).
policy(peer3, ['P', peer2, d1, [], [], [], false]).
policy(peer3, ['F', peer2, d3, [], [], [], false]).
policy(peer3, ['F', peer6, d4, [], [], [], false]).
policy(peer3, ['P', peer4, d4, [], [], [], false]).
policy(peer3, ['P', peer9, d4, [], [], [], false]).
policy(peer3, ['F', peer1, d3, [], [], [], false]).
policy(peer3, ['P', peer1, d5, [], [], [], false]).
policy(peer3, ['P', peer1, d4, [], [], [], false]).
policy(peer3, ['P', peer8, d2, [], [], [], false]).
policy(peer3, ['P', peer7, d4, [], [], [], false]).
policy(peer3, ['P', peer4, d3, [], [], [], false]).
policy(peer3, ['P', peer7, d3, [], [], [], false]).
policy(peer3, ['P', peer2, d2, [], [], [], false]).
policy(peer3, ['F', peer6, d5, [], [], [], false]).
policy(peer3, ['F', peer0, d3, [], [], [], false]).
policy(peer3, ['P', peer7, d2, [], [], [], false]).
policy(peer3, ['P', peer8, d5, [lastRequest(d3, peer4)<41, recordsRequested(d4, peer8)=<52], [], [], false]).
policy(peer3, ['P', peer7, d5, [], [], [], false]).
policy(peer3, ['P', peer8, d1, [], [], [], false]).
policy(peer3, ['F', peer5, d3, [], [], [], false]).
policy(peer3, ['F', peer4, d2, [], [], [], false]).
policy(peer3, ['F', peer1, d2, [], [], [], false]).
policy(peer3, ['P', peer4, d5, [], [], [], false]).
policy(peer3, ['P', peer6, d2, [], [], [], false]).
policy(peer3, ['P', peer5, d4, [], [], [], false]).
policy(peer3, ['P', peer6, d3, [], [], [], false]).
policy(peer3, ['P', peer6, d1, [], [], [], false]).
policy(peer3, ['F', peer1, d1, [], [], [], false]).
policy(peer3, ['P', peer0, d4, [], [], [], false]).
policy(peer3, ['P', peer5, d5, [], [], [], false]).
policy(peer3, ['P', peer4, d1, [], [], [], false]).
policy(peer3, ['P', peer2, d4, [], [], [], false]).
policy(peer3, ['P', peer8, d4, [], [], [], false]).
policy(peer3, ['P', peer5, d1, [], [], [], false]).
policy(peer3, ['P', peer0, d1, [], [], [], false]).
policy(peer3, ['F', peer9, d3, [], [], [], false]).
policy(peer3, ['P', peer5, d2, [], [], [], false]).
policy(peer4, ['P', peer3, d4, [], [], [], false]).
policy(peer4, ['P', peer6, d4, [], [], [], false]).
policy(peer4, ['P', peer5, d2, [], [], [], false]).
policy(peer4, ['P', peer2, d5, [recordsRequested(d1, peer6)>87, requests(d1, peer1)<60], [], [], false]).
policy(peer4, ['F', peer2, d1, [], [], [], false]).
policy(peer4, ['P', peer1, any, [], [], [], false]).
policy(peer4, ['P', peer9, d1, [], [], [], false]).
policy(peer4, ['P', peer5, d3, [], [], [], false]).
policy(peer4, ['F', peer5, d5, [], [], [], false]).
policy(peer4, ['P', peer2, d4, [], [], [], false]).
policy(peer4, ['P', peer0, d5, [], [], [], false]).
policy(peer4, ['P', peer3, d2, [], [], [], false]).
policy(peer4, ['P', peer7, d2, [], [], [], false]).
policy(peer4, ['F', peer0, d3, [recordsRequested(d1, peer5)>41], [], [], false]).
policy(peer4, ['P', peer7, d1, [], [], [[[inform(peer9)], 15, 7]], false]).
policy(peer4, ['P', any, d5, [], [], [], false]).
policy(peer4, ['P', peer3, d3, [], [], [], false]).
policy(peer4, ['P', peer7, d3, [], [], [], false]).
policy(peer4, ['P', peer9, d4, [], [], [], false]).
policy(peer4, ['P', peer5, d1, [], [], [], false]).
policy(peer4, ['P', peer0, d2, [], [], [], false]).
policy(peer5, ['P', peer9, d5, [], [], [], false]).
policy(peer5, ['P', peer7, d5, [], [], [], false]).
policy(peer5, ['F', peer1, d1, [], [], [], false]).
policy(peer5, ['P', peer8, d1, [], [], [[[inform(peer5)], 10, 6]], false]).
policy(peer5, ['P', peer2, d3, [], [], [], false]).
policy(peer5, ['P', peer9, d3, [], [], [], false]).
policy(peer5, ['P', peer2, d5, [], [], [], false]).
policy(peer5, ['P', peer9, d2, [], [], [[[adopt(['P', peer1, d1, [], [], [], true], 3)], 20, 8]], false]).
policy(peer5, ['F', peer1, d4, [], [], [], false]).
policy(peer5, ['P', peer8, d2, [], [], [], false]).
policy(peer5, ['P', peer3, d3, [], [], [], false]).
policy(peer5, ['P', peer2, d1, [], [], [], false]).
policy(peer5, ['P', peer3, d2, [], [], [], false]).
policy(peer5, ['P', peer9, d4, [], [], [], false]).
policy(peer5, ['P', peer0, d4, [], [], [], false]).
policy(peer5, ['P', peer4, d3, [], [], [], false]).
policy(peer5, ['P', peer2, d4, [], [], [], false]).
policy(peer5, ['P', peer9, d1, [], [], [], false]).
policy(peer5, ['P', peer4, d1, [], [], [], false]).
policy(peer5, ['P', peer3, d5, [], [], [], false]).
policy(peer5, ['P', peer6, d2, [], [], [], false]).
policy(peer5, ['P', peer6, d4, [], [], [], false]).
policy(peer5, ['P', peer0, d5, [], [], [], false]).
policy(peer5, ['P', peer8, d4, [], [], [], false]).
policy(peer5, ['P', peer4, d2, [], [], [], false]).
policy(peer5, ['P', peer4, d4, [], [], [], false]).
policy(peer5, ['P', peer7, d3, [], [], [[[obtain(d1, 1)], 25, 8]], false]).
policy(peer5, ['F', peer0, d3, [], [], [], false]).
policy(peer5, ['P', peer6, d1, [], [], [], false]).
policy(peer5, ['P', peer8, d5, [], [], [], false]).
policy(peer5, ['P', peer8, d3, [], [], [], false]).
policy(peer5, ['P', any, d2, [], [], [[[adopt(['F', peer9, d4, [], [], [], true], 2)], 20, 9]], false]).
policy(peer5, ['P', peer6, d3, [], [], [], false]).
policy(peer5, ['F', peer4, d5, [], [], [], false]).
policy(peer5, ['P', peer1, d5, [], [], [[[provide(d3, 10, peer5)], 5, 5]], false]).
policy(peer5, ['P', peer1, d2, [], [], [], false]).
policy(peer5, ['P', peer7, d1, [], [], [], false]).
policy(peer5, ['P', peer1, d3, [], [], [], false]).
policy(peer5, ['P', peer0, d1, [], [], [], false]).
policy(peer5, ['P', peer6, d5, [], [], [], false]).
policy(peer5, ['F', peer7, d4, [], [], [], false]).
policy(peer5, ['P', peer3, d1, [], [], [], false]).
policy(peer5, ['P', peer2, d2, [], [], [], false]).
policy(peer5, ['F', peer7, d2, [], [], [], false]).
policy(peer5, ['F', peer3, d4, [], [], [], false]).
policy(peer6, ['P', peer4, d5, [], [], [], false]).
policy(peer6, ['P', peer1, d5, [], [], [], false]).
policy(peer6, ['P', peer2, d4, [], [], [[[obtain(d1, 8)], 15, 5]], false]).
policy(peer6, ['F', peer5, d5, [], [], [], false]).
policy(peer6, ['F', peer1, d4, [], [], [], false]).
policy(peer6, ['P', peer9, d1, [], [], [], false]).
policy(peer6, ['P', peer8, d3, [], [], [], false]).
policy(peer6, ['P', peer9, d3, [], [], [], false]).
policy(peer6, ['F', peer0, d2, [], [], [], false]).
policy(peer6, ['F', peer5, d3, [], [], [], false]).
policy(peer6, ['P', peer4, d2, [], [], [], false]).
policy(peer6, ['F', peer0, d4, [], [], [], false]).
policy(peer6, ['F', any, d2, [], [], [], false]).
policy(peer6, ['P', peer1, d1, [], [], [], false]).
policy(peer6, ['P', peer9, d2, [], [], [], false]).
policy(peer6, ['P', peer0, d5, [], [], [], false]).
policy(peer6, ['P', peer9, d5, [], [obtain(d2, 4)], [[[obtain(d3, 7)], 10, 5]], false]).
policy(peer6, ['P', peer0, d1, [], [], [], false]).
policy(peer6, ['F', peer3, d5, [], [], [], false]).
policy(peer6, ['P', peer0, d3, [], [], [], false]).
policy(peer6, ['P', peer7, d4, [], [], [], false]).
policy(peer6, ['P', peer4, d3, [], [], [], false]).
policy(peer6, ['P', peer2, d5, [], [], [], false]).
policy(peer6, ['P', peer8, d5, [], [], [], false]).
policy(peer6, ['F', peer4, d4, [], [], [], false]).
policy(peer6, ['P', peer2, d3, [], [], [], false]).
policy(peer6, ['P', peer4, d1, [], [], [], false]).
policy(peer6, ['P', peer5, d1, [], [], [], false]).
policy(peer6, ['F', peer5, d2, [], [], [[[inform(peer6)], 5, 9]], false]).
policy(peer6, ['P', peer9, d4, [], [], [], false]).
policy(peer6, ['F', peer8, d1, [], [], [], false]).
policy(peer6, ['P', peer1, d2, [], [], [], false]).
policy(peer6, ['P', peer1, d3, [], [], [], false]).
policy(peer6, ['P', peer2, d1, [], [], [], false]).
policy(peer6, ['P', peer3, d3, [], [], [], false]).
policy(peer6, ['P', peer5, d4, [], [], [], false]).
policy(peer6, ['F', peer8, d2, [], [], [], false]).
policy(peer6, ['P', peer3, d4, [], [], [], false]).
policy(peer6, ['P', peer3, d1, [], [], [], false]).
policy(peer6, ['P', peer8, d4, [], [], [], false]).
policy(peer6, ['P', peer3, d2, [], [], [], false]).
policy(peer6, ['F', peer7, d2, [], [], [], false]).
policy(peer6, ['F', peer7, d1, [], [], [], false]).
policy(peer6, ['F', peer7, d3, [], [], [], false]).
policy(peer6, ['P', peer7, d5, [], [], [], false]).
policy(peer7, ['P', peer6, d2, [], [], [[[adopt(['P', peer1, d3, [], [], [], false], 1)], 25, 9]], false]).
policy(peer7, ['P', peer9, d1, [], [], [], false]).
policy(peer7, ['P', peer8, d3, [], [], [], false]).
policy(peer7, ['P', peer3, d2, [], [], [], false]).
policy(peer7, ['P', peer5, d2, [], [], [], false]).
policy(peer7, ['F', peer2, d1, [], [], [], false]).
policy(peer7, ['P', peer1, d4, [], [], [], false]).
policy(peer7, ['P', peer9, d2, [], [], [], false]).
policy(peer7, ['P', peer8, d5, [], [], [], false]).
policy(peer7, ['P', peer2, d2, [], [], [], false]).
policy(peer7, ['P', peer3, d1, [], [], [[[provide(d4, 4, peer6)], 10, 8]], false]).
policy(peer7, ['P', peer4, d4, [], [], [], false]).
policy(peer7, ['P', any, d1, [], [], [], false]).
policy(peer7, ['P', peer0, d2, [], [], [], false]).
policy(peer7, ['P', any, d5, [], [], [], false]).
policy(peer7, ['P', peer1, d3, [], [], [], false]).
policy(peer7, ['P', peer9, d3, [], [], [], false]).
policy(peer7, ['P', peer8, d2, [], [], [], false]).
policy(peer7, ['P', peer3, d3, [], [], [], false]).
policy(peer7, ['P', peer5, d5, [], [], [], false]).
policy(peer7, ['P', peer8, d4, [], [], [], false]).
policy(peer7, ['P', peer3, d5, [recordsAccessed(d4, peer5)>=2], [], [], false]).
policy(peer7, ['P', peer3, d4, [], [], [], false]).
policy(peer7, ['P', peer0, d5, [], [], [], false]).
policy(peer7, ['P', peer1, d2, [], [], [], false]).
policy(peer7, ['F', peer4, d3, [], [], [], false]).
policy(peer7, ['P', peer2, d3, [], [], [[[inform(peer7)], 10, 5]], false]).
policy(peer7, ['P', peer0, d1, [], [], [], false]).
policy(peer7, ['P', peer1, d5, [], [], [], false]).
policy(peer7, ['P', peer6, d3, [], [], [], false]).
policy(peer7, ['P', peer5, d3, [], [], [], false]).
policy(peer7, ['F', peer4, d5, [], [], [], false]).
policy(peer7, ['P', peer6, d5, [], [], [], false]).
policy(peer7, ['P', peer0, d3, [], [], [], false]).
policy(peer7, ['P', peer5, d1, [], [], [], false]).
policy(peer7, ['F', peer2, d4, [], [], [], false]).
policy(peer7, ['P', peer4, d1, [], [], [], false]).
policy(peer7, ['P', peer2, d5, [], [], [], false]).
policy(peer7, ['P', peer1, d1, [], [], [], false]).
policy(peer7, ['P', peer4, d2, [], [], [], false]).
policy(peer7, ['F', peer0, d4, [], [], [], false]).
policy(peer8, ['P', peer4, d5, [], [], [], false]).
policy(peer8, ['P', peer4, d2, [], [], [], false]).
policy(peer8, ['P', peer2, d5, [], [], [], false]).
policy(peer8, ['P', peer6, d3, [], [], [], false]).
policy(peer8, ['P', peer6, d4, [], [], [], false]).
policy(peer8, ['P', peer2, d2, [], [], [], false]).
policy(peer8, ['P', peer3, any, [], [], [], false]).
policy(peer8, ['P', peer5, d3, [], [], [], false]).
policy(peer8, ['P', peer5, d2, [], [], [], false]).
policy(peer8, ['P', peer2, d4, [], [], [], false]).
policy(peer8, ['P', peer0, d5, [], [], [], false]).
policy(peer8, ['P', peer5, d1, [], [], [[[adopt(['F', peer7, d4, [lastRequest(d2, peer6)=\=92], [], [], false], 5)], 10, 9]], false]).
policy(peer8, ['P', peer4, d3, [], [], [], false]).
policy(peer8, ['P', peer5, d5, [], [], [], false]).
policy(peer8, ['P', peer0, d4, [], [], [], false]).
policy(peer8, ['P', peer7, d3, [], [], [], false]).
policy(peer8, ['F', peer0, d3, [], [], [], false]).
policy(peer8, ['P', peer2, d1, [], [], [], false]).
policy(peer8, ['P', peer9, d2, [], [], [], false]).
policy(peer8, ['P', peer4, d1, [], [], [[[provide(d1, 1, peer1)], 25, 8]], false]).
policy(peer8, ['P', peer5, d4, [], [], [], false]).
policy(peer8, ['P', peer6, d2, [requests(d4, peer4)=\=44], [], [], false]).
policy(peer8, ['P', peer7, d4, [], [], [], false]).
policy(peer8, ['P', peer4, d4, [], [], [], false]).
policy(peer8, ['F', peer9, d1, [], [], [], false]).
policy(peer8, ['P', peer9, d5, [], [], [], false]).
policy(peer8, ['P', peer0, d1, [], [], [], false]).
policy(peer8, ['P', peer0, d2, [], [], [], false]).
policy(peer8, ['P', peer7, d1, [], [], [], false]).
policy(peer8, ['F', peer6, d1, [requests(d4, peer9)>=84, recordsRequested(d4, peer4)=:=61], [], [], false]).
policy(peer8, ['P', peer9, d3, [], [], [], false]).
policy(peer8, ['P', peer9, d4, [], [], [], false]).
policy(peer8, ['P', peer6, d5, [], [], [], false]).
policy(peer9, ['F', peer4, d5, [], [], [], false]).
policy(peer9, ['P', peer7, d5, [lastAccess(d4, peer2)>91], [], [], false]).
policy(peer9, ['P', peer0, d1, [], [], [], false]).
policy(peer9, ['P', peer2, d5, [], [], [], false]).
policy(peer9, ['F', peer1, d2, [], [], [], false]).
policy(peer9, ['F', peer4, d2, [], [], [], false]).
policy(peer9, ['P', peer3, d1, [], [], [], false]).
policy(peer9, ['P', peer8, d3, [], [], [], false]).
policy(peer9, ['P', peer2, d1, [], [], [], false]).
policy(peer9, ['F', peer8, d2, [], [], [], false]).
policy(peer9, ['F', peer8, d5, [], [], [], false]).
policy(peer9, ['P', peer8, d4, [], [], [], false]).
policy(peer9, ['P', peer5, d1, [], [], [], false]).
policy(peer9, ['P', peer8, d1, [], [], [], false]).
policy(peer9, ['P', peer4, d3, [], [], [], false]).
policy(peer9, ['P', peer3, d5, [], [], [], false]).
policy(peer9, ['P', peer6, d3, [], [], [], false]).
policy(peer9, ['P', peer0, d4, [lastAccess(d2, peer3)=<30], [], [], false]).
policy(peer9, ['P', peer6, d2, [], [], [], false]).
policy(peer9, ['P', peer5, d3, [], [], [], false]).
policy(peer9, ['P', peer0, d5, [], [], [], false]).
policy(peer9, ['P', peer1, d5, [], [], [], false]).
policy(peer9, ['P', peer1, d1, [], [], [], false]).
policy(peer9, ['P', peer3, d2, [], [], [[[obtain(d2, 1)], 15, 9]], false]).
policy(peer9, ['F', peer1, d3, [], [], [], false]).
policy(peer9, ['P', peer4, d4, [], [], [], false]).
policy(peer9, ['P', peer1, d4, [], [], [], false]).
policy(peer9, ['P', peer2, d3, [], [], [], false]).
policy(peer9, ['P', peer7, d1, [lastAccess(d2, peer6)>=36], [], [], false]).
policy(peer9, ['P', peer7, d3, [], [], [], false]).
policy(peer9, ['P', peer6, d1, [requests(d4, peer3)=\=21], [], [], false]).
policy(peer9, ['P', peer0, d3, [], [], [], false]).
policy(peer9, ['P', peer7, d2, [], [], [], false]).
policy(peer9, ['P', peer0, d2, [], [], [], false]).
policy(peer9, ['P', peer5, d4, [], [], [], false]).
policy(peer9, ['P', peer3, d3, [], [], [], false]).
policy(peer9, ['F', peer3, d4, [], [], [], false]).
policy(peer9, ['P', peer7, d4, [], [], [], false]).
policy(peer9, ['P', peer5, d2, [], [], [], false]).
policy(peer9, ['P', peer6, d4, [], [], [], false]).
policy(peer9, ['P', peer6, d5, [], [], [], false]).
policy(peer9, ['P', peer2, d4, [], [], [], false]).
policy(peer9, ['P', peer4, d1, [], [], [], false]).
policy(peer9, ['P', peer5, d5, [], [], [], false]).
policy(peer9, ['P', peer2, d2, [], [], [[[obtain(d4, 9)], 25, 6]], false]).

:- dynamic defaultPermit/2.

defaultPermit(peer0, 'F').
defaultPermit(peer1, 'F').
defaultPermit(peer2, 'F').
defaultPermit(peer3, 'F').
defaultPermit(peer4, 'F').
defaultPermit(peer5, 'F').
defaultPermit(peer6, 'F').
defaultPermit(peer7, 'F').
defaultPermit(peer8, 'F').
defaultPermit(peer9, 'F').