package com.yj.eleccharge.util;

/**
 * TypeAnnotation中允许使用的类型
 */
public enum TypeElement {
    STRING{
        public String toString(){
            return "String";
        }
    },
    LONG{
        public String toString(){
            return "Long";
        }
    },
    DOUBLE{
        public String toString(){
            return "Double";
        }
    },
    INTEGER{
        public String toString(){
            return "Integer";
        }
    },
    Date{
        public String toString(){
            return "Date";
        }
    },
    Float{
        public String toString(){
            return "Float";
        }
    },
    SUBTITLE {
        public String toString(){
            return "Title";
        }
    },
    IGNORE{
        public String toString(){
            return "IGNORE";
        }
    },
    OTHER{
        public String toString(){
            return "Other";
        }
    }

}
