package com.leansoft.nano.sample;

import java.io.FileInputStream;
import java.util.List;

import com.leansoft.nano.IReader;
import com.leansoft.nano.IWriter;
import com.leansoft.nano.NanoFactory;
import com.leansoft.nano.zoo.Animals;
import com.leansoft.nano.zoo.ZooInfo;
import com.leansoft.nano.zoo.animals.Animal;

public class ZooExample {
	
	public static void main(String[] args) {
		try {
			
			IReader xmlReader = NanoFactory.getXMLReader();
			
			ZooInfo zooInfo = xmlReader.read(ZooInfo.class, new FileInputStream("xml/zoo.xml"));
			
			System.out.println("Output after xml read - ");
		    System.out.println("Zoo Name: " + zooInfo.getZooName());
		    System.out.println("Zoo Id: " + zooInfo.getZooId());
		 
		    Animals animals = zooInfo.getAnimals();
		    List<Animal> animalsList = animals.getAnimal();
		 
		    for (Animal animal : animalsList) {
		        System.out.println("\t" + animal.getAnimalName());
		        System.out.println("\t\t" + animal.getAnimalType());
		    }
			
		    IWriter xmlWriter = NanoFactory.getJSONWriter();
		    
		    System.out.println("Output after json write - ");
		    xmlWriter.write(zooInfo, System.out);
		    
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
