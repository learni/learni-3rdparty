//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPPPDFLinkInfo
#define   H_CPPPDFLinkInfo

#include <PDF/Rect.h>
#include <Common/UString.h>

namespace pdftron { 
    namespace PDF {

/**
 * LinkInfo is a utility class used to store information about links when used along
 * with the SetUrlExtraction feature.
 */
class LinkInfo
{
public:
    Rect rect;
    UString url;
};

    };	// namespace PDF
};	// namespace pdftron

#endif
