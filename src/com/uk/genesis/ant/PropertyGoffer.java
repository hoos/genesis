package com.uk.genesis.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

import org.apache.tools.ant.BuildException;


public class PropertyGoffer extends BaseGenesisTask {
        private String name;
        private String type;
    
      
        private List<PropertyMatch> goffer = new ArrayList<PropertyMatch>();

        
        public void addConfiguredGoffer(PropertyMatch propMatch) {
            if (propMatch.getValueset() == null || propMatch.getValueset() == null) {
                throw new BuildException("goffer must specify valueget & valueset", getLocation());
            }
            goffer.add(propMatch);
        }
       
        public void execute() {
            validate();
            try {
                ModelReader modelReader = getGenesisLoader().getModelReader();
                GenesisObjectType type = modelReader.findSingleObjectType(getType());
                if (type == null) {
                    throw new BuildException("Type " + getType() + " is invalid");
                }
                GenesisObject object = type.getInstance(getName());
                Properties props = object.getContentAsProperties();
              
               for (Iterator<PropertyMatch> pm = goffer.iterator(); pm.hasNext();) {
                   PropertyMatch pmval = pm.next();
                   System.out.println("getting : " + pmval.getValueget());
                   System.out.println("setting : " + pmval.getValueset());
                   getProject().setProperty(pmval.getValueset(), props.getProperty(pmval.getValueget()));
                   
                }
            }catch (NullPointerException ne) {
                throw new BuildException(ne.getMessage(), ne, getLocation());
            } catch (ModelException ex) {
                throw new BuildException(ex.getMessage(), ex, getLocation());
            } catch (GenesisObjectNotFoundException ex) {
                throw new BuildException("Object " + getName() + " of type " + getType() + " was not found", 
                        ex, getLocation());
            } catch (IOException ex) {
                throw new BuildException(ex.getMessage(), ex, getLocation());
            }
        
        }
        
        @Override
        protected void validate() {
            super.validate();
            
            if (getName() == null) {
                throw new BuildException("name parameter must be set", getLocation());
            }
            if (getType() == null) {
                throw new BuildException("type parameter must be set", getLocation());
            }
            if (goffer.isEmpty()) {
                throw new BuildException("goffer parameters must be set", getLocation());
            }

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
}

