package com.engagepoint.university.admincentre.util;

/**
 * Class used to store constants.
 * It could not be instantiated - exception will be thrown.
 * @author Roman Garkavenko
 *
 */
public final class Constants {
	
	public final static String COULD_NOT_COMPLETE_CRUD_OPERATION = "Could not complete CRUD operation";
	
	
	private Constants(){
		throw new UnsupportedOperationException("This class should never be instantiated.");
	}
}
