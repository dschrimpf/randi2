package de.randi2.model.fachklassen;

import de.randi2.model.fachklassen.Recht.Rechtenamen;
import org.apache.log4j.Logger;

/**
 * Zentrales Element der Rechteverwaltung.<br>
 * Jedem Benutzerkonto wird eine Rolle zugeordnet, welche bestimmt, weche ueber
 * welche Rechte das Konto verfuegt. <br>
 * Jede Rolle besitzt eine klar definierte Menge an Rechten, welche eine
 * Untermenge der <code>enum</code> {@link Rechtenamen} ist.<br>
 * Die Menge der Rechte einer Rolle werden ueber die Rollenrechtelisten
 * definiert, welche fest codiert sind und somit nicht zur Laufzeit geaendert
 * werden koennen.<br>
 * <br>
 * Ob eine bestimmte Rolle ein bestimmtes Recht enthaelt, kann zur Laufzeit
 * mittels der Methode {@link #besitzenRolleRecht} ermittelt werden (Vgl.
 * Methodendokumentation).<br>
 * <br>
 * Auf die Namen der einzelnen Rollen kann ueber die <code>enum</code>
 * {@link Rollen} zugegriffen werden. <br>
 * Zugegriffen auf die einzelnen Rollen werden kann mittels der jeweiligen
 * Getter.
 * 
 * @version $Id$
 * @author Benjamin Theel <btheel@stud.hs-heilbronn.de>
 * 
 */
public final class Rolle {

	/**
	 * Der logger.
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Enthaelt alle Rollennamen, die innerhalb des Programmes zur Verfuegung
	 * stehen
	 * 
	 * @version 1.0
	 */
	public static enum Rollen {
		/**
		 * Rollenname des Studienarztes
		 */
		STUDIENARZT,
		/**
		 * Rollenname des Studienleiters
		 */
		STUDIENLEITER,
		/**
		 * Rollenname des Administrators
		 */
		ADMIN,
		/**
		 * Rollenname des Systemooperators
		 */
		SYSOP,
		/**
		 * Rollenname des Statistikers
		 */
		STATISTIKER
	}

	// Rollenkonstanten
	/**
	 * Instanz der Rolle "Studienarzt"
	 */
	private static Rolle studienarzt;

	/**
	 * Instanz der Rolle "Studienleiter"
	 */
	private static Rolle studienleiter;

	/**
	 * Instanz der Rolle "Admin"
	 */
	private static Rolle admin;

	/**
	 * Instanz der Rolle "Sysop"
	 */
	private static Rolle sysop;

	/**
	 * Instanz der Rolle "Statistiker"
	 */
	private static Rolle statistiker;

	// Rollenrechtelisten
	/**
	 * Rollenrechteliste des Studienarztes
	 */
	private static Recht[] rechteListeStudienarzt = {
			Recht.getRecht(Rechtenamen.BK_AENDERN),
			Recht.getRecht(Rechtenamen.STUDIENTEILNEHMER_HINZUFUEGEN),
			Recht.getRecht(Rechtenamen.STUDIEN_EINSEHEN),
			Recht.getRecht(Rechtenamen.RANDOMISATION_EXPORTIEREN) };

	/**
	 * Rollenrechteliste des Studienleiters
	 */
	private static Recht[] rechteListeStudienleiter = {
			Recht.getRecht(Rechtenamen.BK_AENDERN),
			Recht.getRecht(Rechtenamen.BK_ANSEHEN),
			Recht.getRecht(Rechtenamen.ZENTREN_ANZEIGEN),
			Recht.getRecht(Rechtenamen.GRUPPENNACHRICHT_VERSENDEN),
			Recht.getRecht(Rechtenamen.STUDIE_ANLEGEN),
			Recht.getRecht(Rechtenamen.STUDIE_AENDERN),
			Recht.getRecht(Rechtenamen.STUDIE_PAUSIEREN),
			Recht.getRecht(Rechtenamen.STUDIENARM_BEENDEN),
			Recht.getRecht(Rechtenamen.STUDIE_SIMULIEREN),
			Recht.getRecht(Rechtenamen.STUDIEN_EINSEHEN),
			Recht.getRecht(Rechtenamen.STAT_EINSEHEN),
			Recht.getRecht(Rechtenamen.RANDOMISATION_EXPORTIEREN),
			Recht.getRecht(Rechtenamen.ARCHIV_EINSEHEN), };

	/**
	 * Rollenrechteliste des Administrators
	 */
	private static Recht[] rechteListeAdmin = {
			Recht.getRecht(Rechtenamen.BK_AENDERN),
			Recht.getRecht(Rechtenamen.BK_SPERREN),
			Recht.getRecht(Rechtenamen.BK_ANSEHEN),
			Recht.getRecht(Rechtenamen.ZENTREN_ANZEIGEN),
			Recht.getRecht(Rechtenamen.ZENTRUM_AENDERN),
			Recht.getRecht(Rechtenamen.ZENTRUM_ANLEGEN),
			Recht.getRecht(Rechtenamen.ZENTRUM_AKTIVIEREN),
			Recht.getRecht(Rechtenamen.GRUPPENNACHRICHT_VERSENDEN),
			Recht.getRecht(Rechtenamen.STUDIE_LOESCHEN),
			Recht.getRecht(Rechtenamen.ARCHIV_EINSEHEN),
			Recht.getRecht(Rechtenamen.STAT_EINSEHEN),
			Recht.getRecht(Rechtenamen.RANDOMISATION_EXPORTIEREN),
			Recht.getRecht(Rechtenamen.STUDIEN_EINSEHEN),
			Recht.getRecht(Rechtenamen.STULEIACCOUNTS_VERWALTEN) };

	/**
	 * Rollenrechteliste des Systemoperators
	 */
	private static Recht[] rechteListeSysop = {
			Recht.getRecht(Rechtenamen.GRUPPENNACHRICHT_VERSENDEN),
			Recht.getRecht(Rechtenamen.SYSTEM_SPERREN),
			Recht.getRecht(Rechtenamen.ADMINACCOUNTS_VERWALTEN) };

	/**
	 * Rollenrechteliste des Statistikers
	 */
	private static Recht[] rechteListeStatistiker = { Recht
			.getRecht(Rechtenamen.STAT_EINSEHEN) };

	// Klassenvariablen

	/**
	 * Name der Rolle, Element der <code>enum</code> {@link Rollen}
	 */
	private Rollen rollenname;

	/**
	 * Liste (ein Array des Types Recht) mit den einzelnen Rechten, ueber die
	 * jeweilige Rolle verfuegt. Ein Recht, welches nicht in dieser Liste
	 * enthalten ist, kann durch die Rolle nicht ausgefuehrt werden
	 */
	private Recht[] rechte;

	// Konstruktor

	/**
	 * Erzeugt eine Instanz einer Rolle mit dem entsprechendem Namen und der
	 * Liste der Rechte, die der Rolle eingeraeumt werden sollen
	 * 
	 * @param name
	 *            Name der Rolle {@link Rollen}
	 * @param rechte
	 *            Liste (entsprechendes Rechte[]) der Rechte, die der Rolle
	 *            eingeraeumt werden.
	 */
	private Rolle(Rollen name, Recht[] rechte) {
		this.rollenname = name;
		this.rechte = rechte;
		logger.debug("Rolle " + this.getName() + " instanziert");
	}

	// Methoden

	/**
	 * Prueft anhand des Rechtenamens {@link Recht.Rechtenamen}, ob die Rolle
	 * das jeweilige Recht besitzt
	 * 
	 * @param name
	 *            Name des Rechtes
	 * @return <code>true</code>, sofern die Rolle das Recht besitzt,
	 *         anderenfalls <code>false</code>
	 */
	public boolean besitzenRolleRecht(Rechtenamen name) {
		for (Recht aRecht : this.rechte) {
			if (name.equals(aRecht.getRechtname())) {
				logger.info("Rolle " + this.getName() + " besitzt Recht "
						+ name.toString());
				return true;
			}
		}
		logger.warn("Rolle " + this.getName() + " besitzt nicht Recht "
				+ name.toString());
		return false;
	}

	/**
	 * Liefert die Instanz der Rolle Admin
	 * 
	 * @return Rolle Admin
	 */
	public static Rolle getAdmin() {
		if (admin == null) {
			admin = new Rolle(Rollen.ADMIN, rechteListeAdmin);
		}
		return admin;
	}

	/**
	 * Liefert die Instanz der Rolle Studienarzt
	 * 
	 * @return Rolle Studienarzt
	 */
	public static Rolle getStudienarzt() {
		if (studienarzt == null) {
			studienarzt = new Rolle(Rollen.STUDIENARZT, rechteListeStudienarzt);
		}
		return studienarzt;
	}

	/**
	 * Liefert die Instanz der Rolle Studienleiter
	 * 
	 * @return Rolle Studienleiter
	 */
	public static Rolle getStudienleiter() {
		if (studienleiter == null) {
			studienleiter = new Rolle(Rollen.STUDIENLEITER,
					rechteListeStudienleiter);
		}
		return studienleiter;
	}

	/**
	 * Liefert die Instanz der Rolle Statistiker
	 * 
	 * @return Rolle Statistiker
	 */
	public static Rolle getStatistiker() {
		if (statistiker == null) {
			statistiker = new Rolle(Rollen.STATISTIKER, rechteListeStatistiker);
		}
		return statistiker;
	}

	/**
	 * Liefert die Instanz der Rolle Systemoperator
	 * 
	 * @return Rolle Systemoperator
	 */
	public static Rolle getSysop() {
		if (sysop == null) {
			sysop = new Rolle(Rollen.SYSOP, rechteListeSysop);
		}
		return sysop;
	}

	/**
	 * Liefert den Namen der Rolle als <code>String<code>
	 * @return Name der Rolle
	 */
	public String getName() {
		return this.rollenname.toString();
	}

	/**
	 * Liefert den Namen der Rolle als Element der <code>enum</code>
	 * {@link Rollen}
	 * 
	 * @return Name der Rolle
	 */
	public Rollen getRollenname() {
		return this.rollenname;
	}

	/**
	 * Liefert den Namen der Rolle mit dem Praefix "Rolle: " als String
	 * 
	 * @return Name der Rolle
	 */
	public String toString() {
		return "Rolle: " + rollenname.toString();
	}

}
