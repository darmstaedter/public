:- module(ast_generator,
	[	pdt_generate_ast/2,
		pdt_forget_ast/1
	]
).
	

:- use_module(library('org/cs3/pdt/util/pdt_util')).
:- use_module(library('org/cs3/pdt/util/pdt_util_context')).
:- use_module(library('spike/pef_base')).
:- use_module(library('spike/builder')).
:- use_module(library('spike/targets/parser')).

:- pdt_define_context(cx(toplevel_ref)).

pdt_builder:build_hook(ast(AbsFile)):-
    ast_generator:my_build_hook(AbsFile).

my_build_hook(AbsFile):-    
	pdt_forget_asts(AbsFile),
	pdt_generate_asts(AbsFile).

pdt_builder:invalidate_hook(parse(AbsFile)):-
    pdt_invalidate_target(ast(AbsFile)).


%%
% pdt_generate_ast(+ToplevelRef, Id).
% 
% generate ast facts for all toplevel terms in File.
% @param ToplevellRef the reference of the toplevel record.
% @param Id is unified with the id of root of the generated syntax tree.

pdt_generate_asts(FileSpec):-
    pdt_file_spec(FileSpec, File),
    pdt_file_ref(File,Ref),

    pdt_with_targets([parse(File)],
    	(	pef_file_query([file_ref=Ref,toplevel_key=Key]),
    		forall(    			
    			pef_toplevel_recorded(Key,[],TlRef),
    			(	pdt_generate_ast(TlRef,Id),
    				pef_toplevel_root_assert([root=Id,toplevel_ref=TlRef,file_ref=Ref])
    			)
    		)
    	)
    ).
    			
pdt_forget_asts(FileSpec):-
    pdt_file_spec(FileSpec, File),
    pdt_file_ref(File,Ref),
    forall(
    	pef_toplevel_root_query([file_ref=Ref,root=Id]),
    	pdt_forget_ast(Id)
    ),
    pef_toplevel_root_retractall([file_ref=Ref]).

%%
% pdt_generate_ast(+ToplevelRef, Id).
% 
% generate ast facts for a toplevel term.
% @param ToplevellRef the reference of the toplevel record.
% @param Id is unified with the id of root of the generated syntax tree.
pdt_generate_ast(TlRef, Id):-
    cx_new(Cx),
    cx_toplevel_ref(Cx,TlRef),
    pef_toplevel_recorded(_,[expanded=Expanded],TlRef),
    term_variables(Expanded,Variables),
    process_variables(Variables,Cx),
    generate_ast(Expanded, Id,Cx).
    
process_variables([],_Cx).
process_variables([Variable|Variables],Cx):-
    pef_reserve_id(pef_variable,Id),
    pef_variable_assert([id=Id]),
    Variable='$var'(Id),
	process_variables(Variables,Cx).
    
generate_ast('$var'(VarId), Id,_Cx):-
    !,
    pef_reserve_id(pef_variable_occurance,Id),
    pef_variable_occurance_assert([id=Id,variable_ref=VarId]).
    
generate_ast(Term, Id,Cx):-
    functor(Term,Name,Arity),
	pef_reserve_id(pef_term,Id),
	pef_term_assert([id=Id,name=Name,arity=Arity]),
	generate_args(1,Arity,Term,Id,Cx).

generate_args(I,N,_Term,_Id,_Cx):-
    I>N,
    !.
generate_args(I,N,Term,Id,Cx):-	
	arg(I,Term,Arg),
	generate_ast(Arg,ArgId,Cx),
	pef_arg_assert([num=I,parent=Id,child=ArgId]),
	J is I + 1,
	generate_args(J,N,Term,Id,Cx).

%% pdt_forget_ast(Id).
% forget the syntax tree with root Id.
%
% removes the complete subtree, including pef_arg edges. Any edge that refers to 
% this subtree as a child is also removed.
% variables that only occur in this subtree are also deleted.
% 	
pdt_forget_ast(Id):-
    pef_variable_occurance_query([id=Id,variable_ref=Ref]),
    !,
    pef_variable_occurance_retractall([id=Id]),
    % if this was the last occurance, remove the variable.
	(	pef_variable_occurance_query([variable_ref=Ref])
	->	true
	;	pef_variable_retractall([id=Ref])
	),
	pef_arg_retractall([child=Id]).
    
pdt_forget_ast(Id):-
	forall(
		pef_arg_query([parent=Id,child=ChildId]),
		pdt_forget_ast(ChildId)
	),
	pef_term_retractall([id=Id]),
	pef_arg_retractall([child=Id]).    
    
    