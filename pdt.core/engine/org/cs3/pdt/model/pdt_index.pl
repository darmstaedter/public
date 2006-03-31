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

:- module(pdt_index,[
	pdt_index_load/2,
	pdt_index_store/2,
	pdt_index_put/4,	
	pdt_index_remove/4,	
	pdt_index_get/3,		
	pdt_index_after/4				
]).

%Currently we only support rb trees as index data structure. Once i found a good way to implement a
%hash table with non-destructive, backtrackable behaviour, there will also be hash index tables.
%Although arbitrary terms may be used for both index key and value, it is recommended to use entity handles as value.
%see pdt_handle.pl

:- use_module(library('org/cs3/pdt/util/pdt_util')).
:- use_module(library('org/cs3/pdt/util/pdt_util_hashtable')).
:- use_module(library('org/cs3/pdt/util/pdt_util_rbtree')).


% pdt_index_load(+IxName,-IX)
%
% load the index table that is associated with the name IxName.
% If no such association exists, a new index table is created and associated with IxName.
pdt_index_load(IxName,IX):-
    pdt_ht_get(index,IxName,IX),!.
pdt_index_load(IxName,IX):-
    pdt_rbtree_new(IX),
    pdt_index_store(IxName,IX).

% pdt_index_store(+IxName,+IX)
%
% Store the index table IX. This will associate the index table with the name IxName. 
% The index table is stored in a way that keeps it unaffected by backtracking.
% If another index table was already associated with IxName, the old table is droped and replaced 
% by the new one.
pdt_index_store(IxName,IX):-
    pdt_ht_set(index,IxName,IX).

% pdt_index_put(+IX,+Key,+Value,-NewIX)
%
% Add the entry Key,Value to the index.
% Does nothing if the Key,Value pair already exists in the index
% Note however that there can be an arbitrary number of entries with the same key, 
% provided the values differ.
% The resulting index table is unified with NewIX.
pdt_index_put(IX,Key,Val,IX):-
    pdt_rbtree_lookup(Key,Val,IX),!.
pdt_index_put(IX,Key,Val,NextIX):-    
    pdt_rbtree_insert(IX,Key,Val,NextIX).


% pdt_index_remove(+IX,+Key,?Value,-NewIX)
%
% Remove all entries matching Key and Value from the index IX.
% The resulting index table will be unified with NewIX
pdt_index_remove(IX,Key,Value,NewIX):-
    pdt_index_get(IX,Key,Value),
    !,
    pdt_rbtree_delete(IX,Key,Value,NextIX),
    pdt_index_remove(NextIX,Key,Value,NewIX).
pdt_index_remove(IX,_,_,IX).
    

% pdt_index_after(+IX, +Key,-Val)
%
% find the first matching entry for Key.
% On backtracking, this will produce all other entries for Key.
pdt_index_get(IX,Key,Val):-
    pdt_rbtree_lookup(Key,Val,IX).

% pdt_index_after(+IX,+Start,-Key,-Val)
%
% find the next neighbour, i.e. the next entry with a key greater then or equal to
% Start. On Backtracking, this will successfully produce all entries after the first one found,
% in standard term order. This can be used to implement interval searches in the index.
pdt_index_after(IX,Start,Key,Val):-
    pdt_rbtree_next(Start,Key,Val,IX).    


