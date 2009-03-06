package de.randi2.model;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import de.randi2.model.exceptions.ValidationException;

@MappedSuperclass
public abstract class AbstractDomainObject implements Serializable {

	public final static int NOT_YET_SAVED_ID = Integer.MIN_VALUE;
	public final static int MAX_VARCHAR_LENGTH = 255;
	@Transient
	private Map<String, Boolean> requiredFields = null;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id = NOT_YET_SAVED_ID;
	@Version
	private int version = Integer.MIN_VALUE;
	private GregorianCalendar createdAt = null;
	private GregorianCalendar updatedAt = null;

	public long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public void setId(long _id) {
		this.id = _id;
	}

	public void setVersion(int _version) {
		this.version = _version;
	}

	public Boolean isRequired(Method m) {

		return m.isAnnotationPresent(org.hibernate.validator.NotEmpty.class) || m.isAnnotationPresent(org.hibernate.validator.NotNull.class) || m.isAnnotationPresent(de.randi2.utility.validations.Password.class);

	}

	public void checkValue(String field, Object value) {
		ClassValidator val = new ClassValidator(this.getClass());
		InvalidValue[] invalids = val.getPotentialInvalidValues(field, value);

		if (invalids.length > 0) {
			throw new ValidationException(invalids);
		}
	}

	public Map<String, Boolean> getRequiredFields() {
		if (requiredFields == null) {
			requiredFields = new HashMap<String, Boolean>();
			for (Method m : this.getClass().getMethods()) {
				if (m.getName().substring(0, 3).equals("get")) {
					requiredFields.put(m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4), this.isRequired(m));
				}
			}
		}
		return requiredFields;
	}

	/**
	 * This method checks if two object identical by matching their id's.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o instanceof AbstractDomainObject) {
			AbstractDomainObject randi2Object = (AbstractDomainObject) o;
			if (randi2Object.id == NOT_YET_SAVED_ID )
				return false;
			if (randi2Object.id == this.id )
				return true;
			return false;
		} else
			return false;
	}

	/*@Override
	public int hashCode() {
		if (this.id != NOT_YET_SAVED_ID) {
			int hash = 7;
			hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
			return hash;
		} else {
			return super.hashCode();
		}
	}*/

	@PreUpdate
	public void beforeUpdate() {
		this.updatedAt = new GregorianCalendar();
	}

	@PrePersist
	public void beforeCreate() {
		this.createdAt = new GregorianCalendar();
	}

	public GregorianCalendar getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(GregorianCalendar createdAt) {
		this.createdAt = createdAt;
	}

	public GregorianCalendar getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(GregorianCalendar updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public abstract String toString();
}