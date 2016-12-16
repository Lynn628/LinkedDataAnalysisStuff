package com.cse.utils.dbpedia.backdata;

public class Judge {
	public static boolean judgePropery(String uri){
		/**
		 * 判断属性是不是类公理（描述类之间关系的属性）、属性公理（描述属性之间关系的属性）
		 */
		boolean b;
		switch (uri){
		case "http://www.w3.org/2000/01/rdf-schema#subClassOF":   
		case "http://www.w3.org/2002/07/owl#equivalentClass":    
		case "http://www.w3.org/2002/07/owl#oneOf":
		case "http://www.w3.org/2002/07/owl#disjointWith":
		case "http://www.w3.org/2002/07/owl#complementOf":
		case "http://www.w3.org/2002/07/owl#unionOf":
		case "http://www.w3.org/2002/07/owl#disjointUnionOf":
		case "http://www.w3.org/2002/07/owl#intersectionOf":
		//以上是类公理
		case "http://www.w3.org/2002/07/owl#inverseOf":
		case "http://www.w3.org/2002/07/owl#equivalentProperty":
		case "http://www.w3.org/2002/07/owl#disjointProperty":
		case "http://www.w3.org/2002/07/owl#propertyChainAxiom":
		case "http://www.w3.org/2000/01/rdf-schema#subPropertyOf": 
		case "http://www.w3.org/2000/01/rdf-schema#range": 
		case "http://www.w3.org/2000/01/rdf-schema#domain":
		//以上是属性公理
		case "http://www.w3.org/2002/07/owl#onProperty":
		case "http://www.w3.org/2002/07/owl#onClass":
		case "http://www.w3.org/2002/07/owl#allValuesFrom":
		case "http://www.w3.org/2002/07/owl#someValuesFrom":
		case "http://www.w3.org/2002/07/owl#hasValue":
		case "http://www.w3.org/2002/07/owl#cardinality":
		case "http://www.w3.org/2002/07/owl#qualifiedCardinality":
		case "http://www.w3.org/2002/07/owl#onDatatype":
		case "http://www.w3.org/2002/07/owl#withRestrictions":
		case "http://www.w3.org/2002/07/owl#hasSelf":
		case "http://www.w3.org/2002/07/owl#hasKey": 
		//以上属性上的类公理
		case "http://www.w3.org/2002/07/owl#sameAs":
		    b=true;
		    break;
		default:
		    b=false;
		}
		return b;
	}
	
	
	public static boolean judgeCountPropery(String uri){
		/**
		 * 判断属性是不是类公理（描述类之间关系的属性）、属性公理（描述属性之间关系的属性）
		 */
		boolean b;
		switch (uri){
		case "http://www.w3.org/2000/01/rdf-schema#subClassOF":   
		case "http://www.w3.org/2002/07/owl#equivalentClass":    
		case "http://www.w3.org/2002/07/owl#oneOf":
		case "http://www.w3.org/2002/07/owl#disjointWith":
		case "http://www.w3.org/2002/07/owl#complementOf":
		case "http://www.w3.org/2002/07/owl#unionOf":
		case "http://www.w3.org/2002/07/owl#disjointUnionOf":
		case "http://www.w3.org/2002/07/owl#intersectionOf":
		//以上是类公理
		case "http://www.w3.org/2002/07/owl#inverseOf":
		case "http://www.w3.org/2002/07/owl#equivalentProperty":
		case "http://www.w3.org/2002/07/owl#disjointProperty":
		case "http://www.w3.org/2002/07/owl#propertyChainAxiom":
		case "http://www.w3.org/2000/01/rdf-schema#subPropertyOf": 
		case "http://www.w3.org/2000/01/rdf-schema#range": 
		case "http://www.w3.org/2000/01/rdf-schema#domain":
		//以上是属性公理
		case "http://www.w3.org/2002/07/owl#onProperty":
		case "http://www.w3.org/2002/07/owl#onClass":
		case "http://www.w3.org/2002/07/owl#allValuesFrom":
		case "http://www.w3.org/2002/07/owl#someValuesFrom":
		case "http://www.w3.org/2002/07/owl#hasValue":
		case "http://www.w3.org/2002/07/owl#cardinality":
		case "http://www.w3.org/2002/07/owl#qualifiedCardinality":
		case "http://www.w3.org/2002/07/owl#onDatatype":
		case "http://www.w3.org/2002/07/owl#withRestrictions":
		case "http://www.w3.org/2002/07/owl#hasSelf":
		case "http://www.w3.org/2002/07/owl#hasKey": 
		//以上属性上的类公理
		    b=true;
		    break;
		default:
		    b=false;
		}
		return b;
	}
	
	public static boolean judgePropertyProperty(String uri){
		/**
		 * 判断是不是属性公理
		 */
		boolean b;
		switch (uri){
		case "http://www.w3.org/2002/07/owl#inverseOf":
		case "http://www.w3.org/2002/07/owl#equivalentProperty":
		case "http://www.w3.org/2002/07/owl#disjointProperty":
		case "http://www.w3.org/2002/07/owl#propertyChainAxiom":
		case "http://www.w3.org/2000/01/rdf-schema#subPropertyOf": 
		case "http://www.w3.org/2000/01/rdf-schema#range": 
		case "http://www.w3.org/2000/01/rdf-schema#domain":
		case "http://www.w3.org/ns/prov#wasDerivedFrom":
		//以上是属性公理
		case "http://www.w3.org/2002/07/owl#onProperty":
		case "http://www.w3.org/2002/07/owl#onClass":
		case "http://www.w3.org/2002/07/owl#allValuesFrom":
		case "http://www.w3.org/2002/07/owl#someValuesFrom":
		case "http://www.w3.org/2002/07/owl#hasValue":
		case "http://www.w3.org/2002/07/owl#cardinality":
		case "http://www.w3.org/2002/07/owl#qualifiedCardinality":
		case "http://www.w3.org/2002/07/owl#onDatatype":
		case "http://www.w3.org/2002/07/owl#withRestrictions":
		case "http://www.w3.org/2002/07/owl#hasSelf":
		case "http://www.w3.org/2002/07/owl#hasKey": 
		//以上属性上的类公理
		    b=true;
		    break;
		default:
		    b=false;
		}
		return b;
	}
	
	public static boolean judgePropertyClass(String uri){
		/**
		 * 判断是不是属性类
		 */
		boolean b;
		switch(uri){
		case "http://www.w3.org/2002/07/owl#ObjectProperty":
		case "http://www.w3.org/2002/07/owl#DatatypeProperty":
		case "http://www.w3.org/2002/07/owl#AnnotationProperty":
		case "http://www.w3.org/2002/07/owl#topObjectProperty":
		case "http://www.w3.org/2002/07/owl#bottomDataProperty":
		case "http://www.w3.org/2002/07/owl#TransitiveProperty":
		case "http://www.w3.org/2002/07/owl#SymmetricProperty":
		case "http://www.w3.org/2002/07/owl#AsymmetricProperty":
		case "http://www.w3.org/2002/07/owl#FunctionalProperty":
		case "http://www.w3.org/2002/07/owl#InverseFunctionalProperty":
		case "http://www.w3.org/2002/07/owl#ReflexiveProperty":
		case "http://www.w3.org/2002/07/owl#IrreflexiveProperty":
		case "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property":
			b=true;
			break;
		default:
			b=false;
		}
		return b;
	}
	
	

}