//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFField
#define   H_CPDFField

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

TRN_API TRN_FieldCreate (TRN_Obj field_dict, TRN_Field* result);
TRN_API TRN_FieldAssign (TRN_Field* left, const TRN_Field* right);
TRN_API TRN_FieldDestroy(TRN_Field* field);
TRN_API TRN_FieldIsValid(const TRN_Field* field, TRN_Bool* result) ;


enum TRN_FieldType
{
	ft_button,       
	ft_check,        
	ft_radio,        
	ft_text,         
	ft_choice,       
	ft_signature,    
	ft_null			
};

TRN_API TRN_FieldGetType(const TRN_Field* field, enum TRN_FieldType* result);
TRN_API TRN_FieldGetValue(TRN_Field* field,TRN_Obj* result);
TRN_API TRN_FieldGetValueAsString(TRN_Field* field, TRN_UString* result);
TRN_API TRN_FieldGetDefaultValueAsString(TRN_Field* field, TRN_UString* result);
TRN_API TRN_FieldSetValueAsString(TRN_Field* field, TRN_UString value);
TRN_API TRN_FieldSetValue(TRN_Field* field, TRN_Obj value);
TRN_API TRN_FieldSetValueAsBool(TRN_Field* field, TRN_Bool value);

TRN_API TRN_FieldGetValueAsBool(const TRN_Field* field, TRN_Bool* result);
TRN_API TRN_FieldRefreshAppearance(TRN_Field* field);
TRN_API TRN_FieldEraseAppearance(TRN_Field* field);
TRN_API TRN_FieldGetDefaultValue(TRN_Field* field, TRN_Obj* result);
TRN_API TRN_FieldGetName(TRN_Field* field, TRN_UString* result);
TRN_API TRN_FieldGetPartialName(TRN_Field* field, TRN_UString* result);
TRN_API TRN_FieldRename(TRN_Field* field, const TRN_UString field_name);
TRN_API TRN_FieldIsAnnot(const TRN_Field* field, TRN_Bool* result);

TRN_API TRN_FieldUseSignatureHandler(TRN_Field* field, const TRN_SignatureHandlerId signature_handler_id, TRN_Obj* result);

enum TRN_FieldFlag
{        
	e_read_only,                
	e_required,
	e_no_export,

	e_pushbutton_flag,          

	e_radio_flag,               
	e_toggle_to_off,            
	e_radios_in_unison,

	e_multiline,                
	e_password,
	e_file_select,
	e_no_spellcheck,
	e_no_scroll,
	e_comb,
	e_rich_text, 
	e_combo,                    
	e_edit,
	e_sort,
	e_multiselect,
	e_commit_on_sel_change
};

TRN_API TRN_FieldGetFlag(const TRN_Field* field, enum TRN_FieldFlag flag, TRN_Bool* result);
TRN_API TRN_FieldSetFlag(TRN_Field* field, enum TRN_FieldFlag flag, TRN_Bool value);

enum TRN_FieldTextJustification 
{
	e_left_justified,
	e_centered,
	e_right_justified
};

TRN_API TRN_FieldGetJustification(TRN_Field* field, enum TRN_FieldTextJustification* result);
TRN_API TRN_FieldSetJustification(TRN_Field* field, enum TRN_FieldTextJustification j);
TRN_API TRN_FieldSetMaxLen(TRN_Field* field, int max_len);
TRN_API TRN_FieldGetMaxLen(const TRN_Field* field, int* result);
TRN_API TRN_FieldGetDefaultAppearance(TRN_Field* field, TRN_GState* result);

TRN_API TRN_FieldGetUpdateRect(const TRN_Field* field, TRN_Rect* result);
TRN_API TRN_FieldFlatten(TRN_Field* field, TRN_Page page);
TRN_API TRN_FieldFindInheritedAttribute (const TRN_Field* field, const char* attrib, TRN_Obj* result);
TRN_API TRN_FieldGetSDFObj (const TRN_Field* field, TRN_Obj* result);

TRN_API TRN_FieldGetOptCount(const TRN_Field* field, int* result);
TRN_API TRN_FieldGetOpt(const TRN_Field* field, int index, TRN_UString* result);

#ifdef __cplusplus
}
#endif

#endif


