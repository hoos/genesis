package com.uk.genesis.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;

public final class ObjectFinder {
	public static String[] findObjectNames(File basePath, String path, String name) {
		String normPath = normalisePath(path);
		List<String> result = new ArrayList<String>();
		
		FileSet set = new FileSet();
		
		set.setProject(new Project());
		set.setDir(basePath.getAbsoluteFile());
		if (normPath != null && normPath.length() > 0) {
			set.appendIncludes(new String[] { path.replaceAll("\\$\\{name\\}", "*") + "/" 
					+ name.replaceAll("\\$\\{name\\}", "*") });
		} else {
			set.appendIncludes(new String[] { name.replaceAll("\\$\\{name\\}", "*") });
		}
		
		Iterator it = set.iterator();
		while (it.hasNext()) {
			FileResource res = (FileResource) it.next();
			
			// We have a candidate file. See if we can calculate the name for it
			String foundName = res.getFile().getName();
			String foundPath = res.getFile().getParentFile().getAbsolutePath();
			
			//String absPath = res.getFile().getAbsolutePath();
			String absBase = basePath.getAbsolutePath();
			
			if (!foundPath.startsWith(absBase)) {
				throw new RuntimeException("Discovered file not within base path");
			}
			
			String relative = foundPath.substring(absBase.length());
			String relativeNorm = normalisePath(relative);
			
			// We now have a found name and a found path
			String nameFromPath = null;
			String nameFromFile = null;
			String guessedObjectName = null;
			if (normPath != null && normPath.contains("${name}")) {
				nameFromPath = findName(normPath, relativeNorm);
				guessedObjectName = nameFromPath;
			}
			if (name.contains("${name}")) {
				nameFromFile = findName(name, foundName);
				guessedObjectName = nameFromFile;
			}
			
			if (guessedObjectName == null) {
				throw new IllegalArgumentException("Neither name nor path specify ${name} token, so name cannot be determined");
			}
			if (nameFromPath == null || nameFromFile == null || nameFromPath.equals(nameFromFile)) {
				// We found a good candidate
				result.add(guessedObjectName);
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	private static String normalisePath(String path) {
		if (path == null) {
			return null;
		}
		
		if (path.startsWith("/") || path.startsWith("\\")) {
			path = path.substring(1);
		}
		if (path.endsWith("/") || path.endsWith("\\")) {
			path = path.substring(0, path.length() - 1);
		}
		
		// Normalise the separators on windows
		path = path.replaceAll("\\\\", "/");
		
		return path;
	}
	
	private static String findName(String pattern, String found) {
		Pattern regex = Pattern.compile(pattern.replaceAll("\\$\\{name\\}", "(.*)"));
		Matcher matcher = regex.matcher(found);
		
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Cannot match found file against base pattern");
		}
		
		return matcher.group(1);
	}
}
