//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CSDFNameTree
#define   H_CSDFNameTree

#ifdef __cplusplus
extern "C" {
#endif 

#include <C/Common/TRN_Types.h>


TRN_API TRN_NameTreeCreate(TRN_SDFDoc doc, const char* name, 
						   TRN_NameTree* result);


TRN_API TRN_NameTreeFind(TRN_SDFDoc doc, const char* name, 
			 TRN_NameTree* result);



TRN_API TRN_NameTreeCreateFromObj (TRN_Obj name_tree, TRN_NameTree* result);


TRN_API TRN_NameTreeCopy (TRN_NameTree const d, TRN_NameTree* result);

	
TRN_API TRN_NameTreeIsValid(TRN_NameTree tree, TRN_Bool* result);



TRN_API TRN_NameTreeGetIterator(TRN_NameTree tree, const TRN_UChar* key, 
						 int key_sz, TRN_DictIterator* result);


TRN_API TRN_NameTreeGetValue(TRN_NameTree tree, const TRN_UChar* key, 
						 int key_sz, TRN_Obj* result);

TRN_API TRN_NameTreeGetIteratorBegin(TRN_NameTree tree, TRN_DictIterator* result);


TRN_API TRN_NameTreePut (TRN_NameTree tree,const TRN_UChar* key, int key_sz, TRN_Obj value);	


TRN_API TRN_NameTreeEraseKey (TRN_NameTree tree,const TRN_UChar* key, int key_sz);


TRN_API TRN_NameTreeErase (TRN_NameTree tree, TRN_DictIterator pos);


TRN_API TRN_NameTreeGetSDFObj (TRN_NameTree const tree,TRN_Obj* result);

#ifdef __cplusplus
}
#endif 

#endif
