//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_PDFTRON_PDF_CPPW_PDFPoint
#define   H_PDFTRON_PDF_CPPW_PDFPoint

namespace pdftron { 
	namespace PDF {


class Point: public TRN_Point
{
	public:
		Point()	{ x=(0); y=(0);}
		Point(double px,double py) { x=(px); y=(py); }
};

	}	// namespace PDF
}	// namespace pdftron

#endif

