/*****************************************************************************
 * This file is part of the Prolog Development Tool (PDT)
 * 
 * WWW: http://sewiki.iai.uni-bonn.de/research/pdt/start
 * Mail: pdt@lists.iai.uni-bonn.de
 * Copyright (C): 2004-2012, CS Dept. III, University of Bonn
 * 
 * All rights reserved. This program is  made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 ****************************************************************************/

:- module(graphML_api,[ prepare_for_writing/2,
						finish_writing/1,
						write_file/5,
						write_call_edge/8,
						write_load_edge/4,
						write_predicates/3,
						write_file_as_element/7]).

:- use_module(util_for_graphML).
:- use_module(pdt_common_pl('metainference/pdt_meta_specification')).
:- use_module(pdt_common_pl('callgraph/pdt_call_graph')).

prepare_for_writing(File,OutStream):-
	reset_id_translation,
    open(File,write,OutStream,[type(text)]),
    write_graphML_header(OutStream),
    write_graphML_ast_keys(OutStream),
    start_graph_element(OutStream),
    flush_output(OutStream).
  
finish_writing(OutStream):-
    close_graph_element(OutStream),
    write_graphML_footer(OutStream),
    close(OutStream).
    
    
write_file(Stream, RelativePath, Predicates, File, Module):-
	get_id(File, Id),
	open_node(Stream, Id),
	write_data(Stream, 'id', Id),
	(	catch(	(	atom_concat(RelativePath,RelativeWithSlash,File),
					atom_concat('/',RelativeFileName,RelativeWithSlash), !
				),
				_, 
				fail
			)
	;	RelativeFileName = File
	),
	write_data(Stream,'fileName',RelativeFileName),
	write_data(Stream,'module',Module),	
	(	Module=user
	->	write_data(Stream,'kind','file')
	;	write_data(Stream,'kind','module')
	),
	start_graph_element(Stream),
	write_predicates(Stream, File, Predicates),
	close_graph_element(Stream),
	close_node(Stream).	

		
write_predicates(Stream, File, all_preds):-
    !,
	forall(	(	predicate_in_file(File, Module, Name, Arity),
				first_line_of_predicate_in_file(Module, Name, Arity, File, Line)
			),
			(	write_predicate(Stream, File, Module, Name, Arity, Line),
				flush_output(Stream)
			)
	).	
write_predicates(Stream, File, PredicatesToWrite):-
	forall(	(	member(Module:Name/Arity,PredicatesToWrite),
				once((file_of_predicate(Module, Name, Arity, File2), File2 == File)),
				first_line_of_predicate_in_file(Module, Name, Arity, File, Line)
			),
			(
				write_predicate(Stream, File, Module, Name, Arity, Line),
				flush_output(Stream)
			)
	).

%write_load_edges(Stream):-
%    forall(load_edge(LoadingFileId,FileId,_,_),
%    	(	(	fileT(LoadingFileId,_,_),
%    			fileT(FileId,_,_)
%    		)
%    	->	write_load_edge(Stream,LoadingFileId,FileId)
%    		%format(Stream,'<edge source="~w" target="~w"/>~n', [LoadingFileId, FileId])
%    	;	format('Problem with load-edge: ~w, ~w~n',[LoadingFileId, FileId])
%	    )
%	).
    
write_file_as_element(Stream, FileId, FilePath, ModuleName, FileType, ExportedStaticPredicates, ExportedDynamicPredicates) :-
    open_node(Stream,FileId),
    write_data(Stream,'kind','file_node'),
    write_data(Stream,'id',FileId),
    write_data(Stream,'file_node_name', ModuleName),
    write_data(Stream,'file_node_path', FilePath),
    write_data(Stream, 'file_node_type', FileType),
    write_data(Stream, 'exported_static_predicates', ExportedStaticPredicates),
    write_data(Stream, 'exported_dynamic_predicates', ExportedDynamicPredicates),
    close_node(Stream).	
    
write_predicate(Stream, File, Module, Name, Arity, Line):-
    get_id(File-Module:Name/Arity, Id),
    open_node(Stream, Id),
    write_data(Stream, 'kind', 'predicate'),
    write_data(Stream, 'id', Id),
	write_data(Stream, 'functor', Name),
	write_data(Stream, 'arity', Arity),	
	write_data(Stream, 'moduleOfPredicate', Module),
	write_data(Stream, 'fileName', File),
	write_data(Stream, 'lineNumber', Line),
	functor(Head, Name, Arity),	
	(	predicate_property(Module:Head, dynamic)
	->	write_data(Stream, 'isDynamic', 'true')
	;	true
	),
	(	predicate_property(Module:Head, transparent)
	->	write_data(Stream, 'isTransparent', 'true')
	;	true
	),	
	(	predicate_property(Module:Head, multifile)
	->	write_data(Stream, 'isMultifile', 'true')
	;	true
	),		
	(	(	predicate_property(Module:Head, meta_predicate(_))
		;	extended_meta_predicate(Module:Head, _)
		)	
	->	write_data(Stream, 'isMetaPredicate', 'true')
	;	true
	),	
	(	exported_predicate(Module, Head)
	->	write_data(Stream, 'isExported', 'true')
	;	true
	),	
	(	locally_dead_predicate(Module, Name, Arity)
	->	write_data(Stream, 'isUnusedLocal', 'true')
	;	true
	),	
	
/*	start_graph_element(Stream),
	write_clauses(Stream,FileName),
	close_graph_element(Stream),
*/	close_node(Stream).

    
write_load_edge(Stream, LoadingFileId, FileId, Imported):-
    open_edge(Stream, FileId, LoadingFileId),
    write_data(Stream, 'kind', 'loading'),
    write_data(Stream, 'imported_predicates', Imported),
    %write_data(Stream, 'kind', 'call'),
	close_edge(Stream).
    
write_call_edge(Stream, SourceModule, SourceName, SourceArity, TargetModule, TargetName, TargetArity, DependentFiles) :-
	functor(SourceHead, SourceName, SourceArity),
	(	predicate_property(SourceModule:SourceHead, multifile)
	->	calls_multifile(TargetModule, TargetName, TargetArity, SourceModule, SourceName, SourceArity, SourceFile, NumberOfCalls)
	;	calls(TargetModule, TargetName, TargetArity, SourceModule, SourceName, SourceArity, NumberOfCalls),
		predicate_property(SourceModule:SourceHead, file(SourceFile))
	),
	once(member(SourceFile, DependentFiles)),
	file_of_predicate(TargetModule, TargetName, TargetArity, TargetFile),
	once(member(TargetFile, DependentFiles)),
	has_id(SourceFile-SourceModule:SourceName/SourceArity, Source),
	has_id(TargetFile-TargetModule:TargetName/TargetArity, Target),
    open_edge(Stream, Source, Target),
    write_data(Stream, 'kind', 'call'),
    write_data(Stream, 'frequency', NumberOfCalls),
	close_edge(Stream),
	fail.
write_call_edge(_, _, _, _, _, _, _, _).
    

write_graphML_header(OutStream):-
	write(OutStream,'<?xml version="1.0" encoding="UTF-8"?>'), nl(OutStream),
	write(OutStream,'<graphml xmlns="http://graphml.graphdrawing.org/xmlns"  
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">'), 
	nl(OutStream).
	
write_graphML_ast_keys(OutStream):-
    write(OutStream, '<key id="id" for="node" attr.name="id" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="kind" for="all" attr.name="kind" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="fileName" for="node" attr.name="description" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="lineNumber" for="node" attr.name="lineNumber" attr.type="int">'),
    nl(OutStream),
  	write(OutStream, '    <default>-1</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),
    write(OutStream, '<key id="file_node_name" for="node" attr.name="file_node_name" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="file_node_path" for="node" attr.name="file_node_path" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="file_node_type" for="node" attr.name="file_node_type" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="module" for="node" attr.name="module" attr.type="string">'),
    nl(OutStream),
  	write(OutStream, '    <default>user</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),
    write(OutStream, '<key id="functor" for="node" attr.name="functor" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="arity" for="node" attr.name="arity" attr.type="int"/>'),
    nl(OutStream),
    write(OutStream, '<key id="moduleOfPredicate" for="node" attr.name="moduleOfPredicate" attr.type="string"/>'),
    nl(OutStream),
    write(OutStream, '<key id="isTransparent" for="node" attr.name="isTransparent" attr.type="boolean">'),
    nl(OutStream),
    write(OutStream, '    <default>false</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),
    write(OutStream, '<key id="isDynamic" for="node" attr.name="isDynamic" attr.type="boolean">'),
    nl(OutStream),
    write(OutStream, '    <default>false</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),   
    write(OutStream, '<key id="isMultifile" for="node" attr.name="isMultifile" attr.type="boolean">'),
    nl(OutStream),
    write(OutStream, '    <default>false</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
  	nl(OutStream),
    write(OutStream, '<key id="isMetaPredicate" for="node" attr.name="isMetaPredicate" attr.type="boolean">'),
    nl(OutStream),
    write(OutStream, '    <default>false</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),
    write(OutStream, '<key id="isUnusedLocal" for="node" attr.name="isUnusedLocal" attr.type="boolean">'),
    nl(OutStream),
    write(OutStream, '    <default>false</default>'),
  	nl(OutStream),  	
  	write(OutStream, '</key>'),
    nl(OutStream),
    write(OutStream, '<key id="isExported" for="node" attr.name="isExported" attr.type="boolean">'),
    nl(OutStream),
    write(OutStream, '    <default>false</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),   
    write(OutStream, '<key id="frequency" for="edge" attr.name="frequency" attr.type="int">'),
    nl(OutStream),
    write(OutStream, '    <default>1</default>'),
  	nl(OutStream),
  	write(OutStream, '</key>'),
    nl(OutStream),
    write(OutStream, '<key id="exported_static_predicates" for="node" attr.name="exported_static_predicates" attr.type="string" />'),
    nl(OutStream),
    write(OutStream, '<key id="exported_dynamic_predicates" for="node" attr.name="exported_dynamic_predicates" attr.type="string" />'),
    nl(OutStream),
    write(OutStream, '<key id="imported_predicates" for="edge" attr.name="imported_predicates" attr.type="string" />'),
    nl(OutStream),
    nl(OutStream).
    

write_graphML_footer(OutStream):-
    write(OutStream,'</graphml>').
    

    
start_graph_element(OutStream):-
    write(OutStream,'<graph edgedefault="directed">'), 
    nl(OutStream).

close_graph_element(OutStream):-
    write(OutStream,'</graph>'), 
    nl(OutStream).
    
    
    
open_node(Stream,Id):-
    format(Stream, '<node id="~w">~n', [Id]).

close_node(Stream):-
    write(Stream, '</node>'),
    nl(Stream).
   
open_edge(Stream,Source,Target):-
    format(Stream, '<edge source="~w" target="~w">~n', [Source, Target]). 
	
close_edge(Stream):-
    write(Stream, '</edge>'),
    nl(Stream).

write_data(Stream,Key,Value):-
	format(Stream, '   <data key="~w">~w</data>~n', [Key,Value]).	
	
:- dynamic(pred_to_id/5).
:- dynamic(atom_to_id/2).
:- dynamic(current_id/1).

reset_id_translation :-
	retractall(pred_to_id(_,_,_,_,_)),
	retractall(atom_to_id(_,_)),
	retractall(current_id(_)),
	assertz(current_id(1)).

get_new_id(NewId) :-
	var(NewId),
	retract(current_id(NewId)),
	succ(NewId, NextId),
	assertz(current_id(NextId)),
	!.

get_id(File-Module:Name/Arity, Id) :-
	!,
	(	pred_to_id(File, Module, Name, Arity, Id)
	->	true
	;	get_new_id(Id),
		assertz(pred_to_id(File, Module, Name, Arity, Id))
	).

get_id(Atom, Id) :-
	(	atom_to_id(Atom, Id)
	->	true
	;	get_new_id(Id),
		assertz(atom_to_id(Atom, Id))
	).

has_id(File-Module:Name/Arity, Id) :-
	!,
	pred_to_id(File, Module, Name, Arity, Id).
has_id(Atom, Id) :-
	atom_to_id(Atom, Id).