//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFFunction
#define   H_CPDFFunction

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

TRN_API TRN_FunctionCreate (TRN_Obj funct_dict, TRN_Function* result);
	
TRN_API TRN_FunctionAssign(TRN_Function left, TRN_Function right);

TRN_API TRN_FunctionDestroy(TRN_Function f);

enum TRN_FunctionType
{
	e_sampled     = 0, // uses a table of sample values to define the function.
	e_exponential = 2, // defines a set of coefficients for an exponential function.
	e_stitching   = 3, // a combination of other functions, partitioned across a domain.
	e_postscript  = 4  // A PostScript calculator function.
};

TRN_API TRN_FunctionGetType(const TRN_Function f, enum TRN_FunctionType* result);

TRN_API TRN_FunctionGetInputCardinality(TRN_Function f, int* result);

TRN_API TRN_FunctionGetOutputCardinality(TRN_Function f, int* result);

TRN_API TRN_FunctionEval(TRN_Function f, double *in, double *out);

TRN_API TRN_FunctionGetSDFObj (TRN_Function f, TRN_Obj* result);

#ifdef __cplusplus
}
#endif

#endif
