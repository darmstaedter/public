%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% This file is part of the Prolog Development Tool (PDT)
% 
% Author: Lukas Degener (among others) 
% E-mail: degenerl@cs.uni-bonn.de
% WWW: http://roots.iai.uni-bonn.de/research/pdt 
% Copyright (C): 2004-2006, CS Dept. III, University of Bonn
% 
% All rights reserved. This program is  made available under the terms 
% of the Eclipse Public License v1.0 which accompanies this distribution, 
% and is available at http://www.eclipse.org/legal/epl-v10.html
% 
% In addition, you may at your option use, modify and redistribute any
% part of this program under the terms of the GNU Lesser General Public
% License (LGPL), version 2.1 or, at your option, any later version of the
% same license, as long as
% 
% 1) The program part in question does not depend, either directly or
%   indirectly, on parts of the Eclipse framework and
%   
% 2) the program part in question does not include files that contain or
%   are derived from third-party work and are therefor covered by special
%   license agreements.
%   
% You should have received a copy of the GNU Lesser General Public License
% along with this program; if not, write to the Free Software Foundation,
% Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
%   
% ad 1: A program part is said to "depend, either directly or indirectly,
%   on parts of the Eclipse framework", if it cannot be compiled or cannot
%   be run without the help or presence of some part of the Eclipse
%   framework. All java classes in packages containing the "pdt" package
%   fragment in their name fall into this category.
%   
% ad 2: "Third-party code" means any code that was originaly written as
%   part of a project other than the PDT. Files that contain or are based on
%   such code contain a notice telling you so, and telling you the
%   particular conditions under which they may be used, modified and/or
%   distributed.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

/**
provides predicates for dealing with the subterm position 
terms generated by read_term/{2,3}
*/
:- module(pdt_util_term_position,[
	top_position/3,
	sub_position/3,
	sub_positions/2
]).

/*
top_position(+PositionTerm, -From, -To)

retrieve the position of the outer-most 
term from a position term.
*/
top_position(From-To,From,To).
top_position(string_position(From,To),From,To).
top_position(brace_term_position(From,To,_),From,To).
top_position(list_position(From,To,_,_),From,To).	
top_position(term_position(From,To,_,_,_),From,To).	


/*
sub_position(+PositionTerm, +Arg, -SubPosition)

retrieve the position term for the Arg-th argument term 
from a position term. 

The argument Arg should be an integer. The first subterm is 
addressed with Arg=1. Argument terms should always be adressed
as if the underlying term was in a canonical representation.
I.e. lists are nested '.'/2 terms, etc.

This predicate will handle list and operator terms accordingly while 
maintaining the original positions as precisely as possible.
The only situation where this gets a little complicated are lists
as nested sublists may not have a concrete textual representation. 
In those cases this predicate aproximates the sublist position by the 
position of the included elements.
*/

sub_position(brace_term_position(_,_,T),1,T).
sub_position(list_position(_,_,[H|_],_),1,H).		
sub_position(list_position(_,_,[_|[]],_),2,none).		
sub_position(list_position(_,_,[_|[First|T]],none),2,list_position(From,To,[First|T],none)):-
	last([First|T],Last),
	top_position(First,From,_),
	top_position(Last,_,To).	
sub_position(list_position(_,_,Elms,Tail),2,list_position(From,To,[First|T],none)):-
    \+Tail=none,
    append(Elms,[Tail],[_|[First|T]]),
    last([First|T],Last),
	top_position(First,From,_),
	top_position(Last,_,To).	    
sub_position(term_position(_,_,_,_,ArgPositions),N,SubPos):-
	nth1(N,ArgPositions,SubPos).
	

	
sub_positions(brace_term_position(_,_,T),[T]).
sub_positions(term_position(_,_,_,_,ArgPositions),ArgPositions).
sub_positions(list_position(_,_,Elms,Tail),[SubHead,SubTail]):-    
	sub_position(list_position(_,_,Elms,Tail),1,SubHead),
	sub_position(list_position(_,_,Elms,Tail),2,SubTail).
	