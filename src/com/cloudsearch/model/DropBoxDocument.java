package com.cloudsearch.model;

import com.dropbox.client2.DropboxAPI.Entry;
import com.owlike.genson.annotation.JsonIgnore;

public class DropBoxDocument {

        private String name;
        private String dateModified;
        private String path; 
        private long size; 
        private boolean isDir; 
        @JsonIgnore
        private Entry entry; 
        private String parentPath;
		private long bytes;
		private String icon;
		private boolean isDeleted;
		private String mimeType; 
        private String data ;
		private String id;
		
        public DropBoxDocument(Entry e){                
                this.entry = e;                 
                this.size = e.bytes; 
                this.isDir = e.isDir;
                this.dateModified =  e.modified;
                this.path = e.path;
                this.parentPath = e.parentPath(); 
                this.name = e.fileName(); 
                this.setBytes(e.bytes);
                this.setIcon(e.icon);
                this.setDeleted(e.isDeleted);
                this.setMimeType(e.mimeType);
                this.setId(e.rev);
                
                                 
        }
        
        
        /**
         * Returns the parent path of this file
         * @return the parent path of this file
         */
        public String getParentPath(){
                
                return parentPath; 
        }
        /**
         * Returns the file name
         * @return the file name
         */
        public String getFileName(){
                return name; 
        }
        
        /**
         * Gets the last date modified
         * @return last date modified
         */
        public String getDateModified(){
                return dateModified; 
        }
        
        /**
         * Gets the full path of this file
         * @return the full path of this file
         */
        public String getPath(){
                return path; 
        }
        
        
        /**
         * Gets the size of this file
         * @return the size of this file
         */
        public long getSize() {
                return size; 
        }
        
        /**
         * Returns the underlying dropbox entry file object tha's being encapsulated
         * @return the underlying dropbox entry object
         */
        public Entry getDropBoxEntryObject(){
                
                return entry; 
        }
        
        
        
        /**
         * Returns true if this file is a directory
         * @return true if this file is a directory
         */
        public boolean isDir(){
                return isDir; 
        }
        
        
        /**
         * Returns true if this file is NOT a directory
         * @return true if this file is NOT a directory
         */
        public boolean isAFile(){
                return !isDir; 
        }


		public long getBytes() {
			return bytes;
		}


		public void setBytes(long bytes) {
			this.bytes = bytes;
		}


		public String getIcon() {
			return icon;
		}


		public void setIcon(String icon) {
			this.icon = icon;
		}


		public boolean isDeleted() {
			return isDeleted;
		}


		public void setDeleted(boolean isDeleted) {
			this.isDeleted = isDeleted;
		}


		public String getMimeType() {
			return mimeType;
		}


		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}


		public String getData() {
			return data;
		}


		public void setData(String data) {
			this.data = data;
		}


		public String getId() {
			return id;
		}


		public void setId(String id) {
			this.id = id;
		}


		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DropBoxDocument [name=");
			builder.append(name);
			builder.append(", dateModified=");
			builder.append(dateModified);
			builder.append(", path=");
			builder.append(path);
			builder.append(", size=");
			builder.append(size);
			builder.append(", isDir=");
			builder.append(isDir);
			builder.append(", entry=");
			builder.append(entry);
			builder.append(", parentPath=");
			builder.append(parentPath);
			builder.append(", bytes=");
			builder.append(bytes);
			builder.append(", icon=");
			builder.append(icon);
			builder.append(", isDeleted=");
			builder.append(isDeleted);
			builder.append(", mimeType=");
			builder.append(mimeType);
			builder.append("]");
			return builder.toString();
		}
        
}