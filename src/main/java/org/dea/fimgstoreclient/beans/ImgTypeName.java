package org.dea.fimgstoreclient.beans;

/**
 * 
 * enum containing all precomputed image types in the Fimagestore
 * element names equal the URL parameter values in fileType.
 * 
 *  
 * fileType=metadata is handled separately
 * 
 * @author philip
 *
 */
public enum ImgTypeName {
	orig, browser, view, thumb, scale50, scale25, bin;
}