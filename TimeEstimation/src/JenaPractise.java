import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.VCARD;

public class JenaPractise {
	 public static void main(String[] args){
		   String personURI = "http://somwhere/JohnSmith";
		   String givenName = "John";
		   String familyName = "Smith";
		   String fullName = givenName + "" + familyName;
		   String nsA = "http://somewhere/else#";
		   String nsB = "http://nowhere/else#";
	 
		   //Create an empty Model
		   Model model = ModelFactory.createDefaultModel();
		   //Create the resource
		   Resource johnSmith = model.createResource(personURI)
				   					.addProperty(VCARD.FN, fullName)
				   					.addProperty(VCARD.N ,
				   							model.createResource()
				   							.addProperty(VCARD.Given, givenName)
				   							.addProperty(VCARD.Family, familyName));
		   //List the statements in the Model
		   StmtIterator iter = model.listStatements();
		   
		   //Print out the predicate, subject and object of each statment
		   while(iter.hasNext()){
			   Statement stmt = iter.nextStatement();//get next statement
			   Resource subject = stmt.getSubject();//get the subject
			   Property predicate = stmt.getPredicate();//get the predicate
			   RDFNode object = stmt.getObject();//get the object
			  
			   System.out.print(subject.toString());
			   System.out.print(""+predicate.toString()+"");
			   if(object instanceof Resource){
				   System.out.print(object.toString());
			   }else{
				   //object is a literal
				   System.out.print("\""+object.toString()+"\"");
			   }
			   System.out.println(" .");
			  
			   Resource root = model.createResource(nsA + "root");
			   Property P = model.createProperty(nsA + "P");
			   Property Q = model.createProperty(nsB + "Q");
			   Resource x = model.createProperty(nsA + "x");
			   Resource y = model.createProperty(nsA + "y");
			   Resource z = model.createProperty(nsA + "z");
			   model.add(root, P, x).add(root, P, y).add(y, Q, z);
			   System.out.println("#-- no special prefixes defined");
			   model.write(System.out);
			   System.out.println("#-- nsA defined");
			   model.setNsPrefix("nsA", nsA);
			   model.write(System.out);
			   System.out.println("#-- nsA and cat defined");
			   model.setNsPrefix("cat", nsB);
			   model.write(System.out);
		   }	   
	   }
}
