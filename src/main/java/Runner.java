import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import model.Person;

public class Runner {

	static HashMap<String, String> dbs;
	static TransactionManager tm;

	public static void main(String[] args) {

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

		dbs = new HashMap<>();
		//		dbs.put( "mongodb-local", "mongodb" );
		//		dbs.put( "neo4j-local-bolt", "neo4j" );
		//		dbs.put( "neo4j-embedded", "neo4j" );
//		dbs.put( "infinispan-embedded", "infinispan" );
		dbs.put( "infinispan-with-hotrod", "infinispan" );
//
		for ( String key : dbs.keySet() ) {
			System.out.println( "\n\n\n\n\n" );
			System.out.println(
					"-------------------------------------------------------- " + key + " --------------------------------------------------------" );
			runIt( key );
		}
	}

	private static void runIt(String db) {
		//build the EntityManagerFactory as you would build in in Hibernate Core
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( db );

		//Persist entities the way you are used to in plain JPA
		try {
			EntityManager em = emf.createEntityManager();
			em.clear();

			ArrayList<Person> people = Person.createPeople();

			deletePeople( em, dbs.get( db ), people );

			tm.begin();
			for ( Person p : people ) {
				em.persist( p );
			}
			em.flush();
			em.clear();
			em.close();
			tm.commit();

			//Retrieve your entities the way you are used to in plain JPA
			tm.begin();
			em = emf.createEntityManager();
			Person husband = em.find( Person.class, "Nick" );
			for ( Person p : husband.getSpouse().getChildren() ) {
				if ( p.getName().equals( "Tom" ) && p.getSpouse().getName().equals( "Mary" ) ) {
					System.out.println(
							"_____________________________________________________" + "SUCCESS" + "_____________________________________________________" );
				}

			}

			em.flush();
			em.close();
			tm.commit();

			emf.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deletePeople(EntityManager em, String db, ArrayList<Person> people)
			throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
		tm.begin();

		if ( db.equals( "infinispan" ) ) {
			for ( Person p : people ) {
				em.remove( p );
			}
		}
		else {
			Query q = em.createNativeQuery( getNativeDeletePersonQuery( dbs.get( db ) ) );
			q.executeUpdate();
		}

		em.flush();
		tm.commit();
	}

	private static String getNativeDeletePersonQuery(String db) {
		switch ( db ) {
			case "mongodb":
				return "db.Person.remove({ })";
			case "neo4j":
				return "MATCH (n) DETACH DELETE n";
			case "infinispan":
				return "delete * from Person";
		}
		return null;
	}

}
