package de.randi2.jsf.supportBeans;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import lombok.Getter;
import lombok.Setter;

import de.randi2.jsf.controllerBeans.LoginHandler;
import de.randi2.model.enumerations.Gender;
import de.randi2.model.randomization.BlockRandomizationConfig;

public class Enums {

	private LoginHandler loginHandler;
	
	@Setter @Getter
	private List<SelectItem> genderItems;

	public Enums() {
		
		loginHandler =  ((LoginHandler) FacesContext.getCurrentInstance()
				.getApplication().getELResolver().getValue(
						FacesContext.getCurrentInstance()
								.getELContext(), null, "loginHandler"));
		
		genderItems = new ArrayList<SelectItem>(Gender.values().length);
		for (Gender g : Gender.values()) {
			genderItems.add(new SelectItem(g, localizeGenderEntry(g)));
		}

		blockSizeTypeItems = new ArrayList<SelectItem>(
				BlockRandomizationConfig.TYPE.values().length);
		for (BlockRandomizationConfig.TYPE t : BlockRandomizationConfig.TYPE
				.values()) {
			blockSizeTypeItems.add(new SelectItem(t,
					localizeBlockSizeTypeItemsEntry(t)));
		}
	}

	private String localizeGenderEntry(Gender g) {
		return ResourceBundle.getBundle("de.randi2.jsf.i18n.gender",
				loginHandler.getChosenLocale()).getString(g.toString());
	}

	@Getter
	private List<SelectItem> blockSizeTypeItems;

	private String localizeBlockSizeTypeItemsEntry(
			BlockRandomizationConfig.TYPE t) {
		return ResourceBundle.getBundle("de.randi2.jsf.i18n.algorithms",
				loginHandler.getChosenLocale()).getString(
				BlockRandomizationConfig.class.getCanonicalName() + "."
						+ t.toString());
	}

}
