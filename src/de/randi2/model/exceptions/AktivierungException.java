package de.randi2.model.exceptions;

/**
  * <p>
 * Diese Klasse kapselt die Fehler, die innerhalb der Klasse Aktivierung auftreten koennen.
 * </p>
 * @author Andreas Freudling [afreudling@stud.hs-heilbronn.de]
 * @version $Id: AktivierungException.java 2448 2007-05-07 13:45:09Z afreudli $
 *
 */
public class AktivierungException extends BenutzerException{
	
	/**
	 * Erstellt eine Exception und haengt eine Fehlermeldung an.
	 * 
	 * @param fehlermeldung Fehlermeldung die an die Exception angehaengt wird.
	 */
	public AktivierungException(String fehlermeldung){
		super(fehlermeldung);
	}

}