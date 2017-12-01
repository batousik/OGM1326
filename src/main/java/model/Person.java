package model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Person {

	@Id
	private String name;

	@OneToOne
	private Person spouse;

	@OneToMany
	private List<Person> children;

	public Person() {

	}

	public Person(String name, Person spouse, List<Person> children) {
		this.name = name;
		this.spouse = spouse;
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person getSpouse() {
		return spouse;
	}

	public void setSpouse(Person spouse) {
		this.spouse = spouse;
	}

	public List<Person> getChildren() {
		return children;
	}

	public void setChildren(List<Person> children) {
		this.children = children;
	}

	public static ArrayList<Person> createPeople() {
		ArrayList<Person> people = new ArrayList();

		Person childTom = new Person();
		childTom.setName( "Tom" );

		Person childLouis = new Person();
		childLouis.setName( "Louis" );

		Person childAlex = new Person();
		childAlex.setName( "Alex" );

		Person mary = new Person();
		mary.setName( "Mary" );

		mary.setSpouse( childTom );
		childTom.setSpouse( mary );

		ArrayList<Person> children = new ArrayList();
		children.add( childLouis );
		children.add( childTom );
		children.add( childAlex );

		Person husband = new Person();
		husband.setName( "Nick" );

		Person wife = new Person();
		wife.setName( "Kate" );

		husband.setSpouse( wife );
		wife.setSpouse( husband );

		husband.setChildren( children );
		wife.setChildren( children );

		people.add( husband );
		people.add( wife );
		people.add( childAlex );
		people.add( childLouis );
		people.add( childTom );
		people.add( mary );
		return people;
	}
}
