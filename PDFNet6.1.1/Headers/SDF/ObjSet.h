//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_SDFObjSet
#define   H_SDFObjSet

#include <SDF/Obj.h>
#include <C/SDF/TRN_ObjSet.h>

namespace pdftron { 
	namespace SDF {

/**
 * ObjSet is a lightweight container that can hold a collection of SDF objects.
 */
class ObjSet
{
public:
	ObjSet();
	~ObjSet();

	/**
	 * Create a new name object in this object set.
	 */
	Obj CreateName(const char* name);

	/**
	 * Create a new array object in this object set.
	 */
	Obj CreateArray();

	/**
	 * Create a new boolean object in this object set.
	 */
	Obj CreateBool(bool value);

	/**
	 * Create a new dictionary object in this object set.
	 */
	Obj CreateDict();

	/**
	 * Create a new null object in this object set.
	 */
	Obj CreateNull();

	/**
	 * Create a new number object in this object set.
	 */
	Obj CreateNumber(double value);

	/**
	 * Create a new string object in this object set.
	 */
	Obj CreateString(const UString& value);

	/**
	 * Frees the native memory of the object.
	 */
	void Destroy();

private:
	TRN_ObjSet mp_set;
};


#include <Impl/ObjSet.inl>
	};	// namespace SDF
};	// namespace pdftron

#endif
