package de.randi2.model.exceptions;

/**
 * <p>
 * Diese Klasse kapselt die Fehler, die innerhalb der Klasse Person auftreten
 * koennen.
 * </p>
 * 
 * @author Thomas Willert <twillert@stud.hs-heilbronn.de>
 * @version $Id: PersonException.java 1287 2007-02-05 14:42:00Z twillert $
 * 
 */
public class PersonException extends Exception {

	/*
	 * Die einzelnen Konstanten werde sich auch im Laufe der Arbeit ergeben.
	 */

	/**
	 * Diese Konstante wird uebergeben, wenn der Nachname fehlt.
	 */
	public static final String NACHNAME_FEHLT = "Bitte Nachname eingeben.";

	// TODO: in der meldung stehen dann spater noch die echten konventionen
	/**
	 * Diese Konstante wird uebergeben, wenn der der Nachname nicht den
	 * Konventionen.
	 */
	public static final String NACHNAME_UNGUELTIG = "Nachname entspricht nicht den Konventionen";

	/**
	 * Diese Konstante wird uebergeben, wenn der Vorname fehlt.
	 */
	public static final String VORNAME_FEHLT = "Bitte Vorname eingeben.";

	/**
	 * Diese Konstante wird uebergeben, wenn der Vorname nicht den Konventionen entspricht.
	 */
	public static final String VORNAME_UNGUELTIG = "Vorname entspricht nicht den Konventionen.";

	/**
	 * Diese Konstante wird uebergeben, wenn das Geschlecht fehlt.
	 */
	public static final String GESCHLECHT_FEHLT = "Bitte Geschlecht eingeben.";

	/**
	 * Diese Konstante wird uebergeben, wenn die E-Mail-Adresse fehlt.
	 */
	public static final String EMAIL_FEHLT = "Bitte E-Mail-Adresse eingeben.";

	/**
	 * Diese Konstante wird uebergeben, wenn die E-Mail-Adresse ungueltig ist.
	 */
	public static final String EMAIL_UNGUELTIG = "Email ist ungültig.";

	/**
	 * Diese Konstante wird uebergeben, wenn die Telefonnumer fehlt.
	 */
	public static final String TELEFONNUMMER_FEHLT = "Bitte Telefonnummer eingeben.";

	/**
	 * Diese Konstante wird uebergeben, wenn die Telefonnummer nicht den Konventionen entspricht.
	 */
	public static final String TELEFONNUMMER_UNGUELTIG = "Telefonnummer entspricht nicht den Konventionen.";

	/**
	 * Diese Konstante wird uebergeben, wenn die Faxnummer nicht den Konventionen entspricht.
	 */
	public static final String FAX_UNGUELTIG = "Fax enstspricht nicht den Konventionen.";

	/**
	 * Diese Konstante wird uebergeben, wenn das Geschlecht nicht den Konventionen.
	 */
	public static final String GESCHLECHT_UNGUELTIG = "Geschlecht entspricht nicht den Konventionen.";

	/**
	 * Diese Konstante wird uebergeben, wenn die Handynummer nicht den Konventionen entspricht.
	 */
	public static final String HANDY_UNGUELTIG = "Handynummer entspricht nicht den Konventionen.";

	/**
	 * Diese Konstante wird uebergeben, wenn der der Titel nicht den Konventionen entsprciht.
	 */
	public static final String TITEL_UNGUELTIG = "Titel entspricht nicht den Konventionen.";

	/**
	 * Diese Konstante wird uebergeben, wenn ein unbekannter Fehler aufgetreten ist.
	 */
	public static final String FEHLER = "Erstmal fuer alles, bei dem mir nix anderes Sinnvolles einfaellt!!!";

	/**
	 * Ein Konstruktor dieser Klasse
	 * 
	 * @param arg0
	 *            eine Konstante aus dieser Klasse als Message
	 */
	public PersonException(String arg0) {
		super(arg0);
	}

}