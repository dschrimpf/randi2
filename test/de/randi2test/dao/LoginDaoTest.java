package de.randi2test.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import de.randi2.dao.LoginDao;
import de.randi2.model.AbstractDomainObject;
import de.randi2.model.Login;
import de.randi2test.model.util.ObjectFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring-test.xml"})
@Transactional
public class LoginDaoTest {

	@Autowired private LoginDao loginDao;	
	@Autowired private ObjectFactory factory;
	
	@Test
	public void CreateAndSaveTest(){
		
		Login l = factory.getLogin();
		
		assertEquals(AbstractDomainObject.NOT_YET_SAVED_ID, l.getId());
		loginDao.save(l);
		assertNotSame(AbstractDomainObject.NOT_YET_SAVED_ID, l.getId());
		
		assertNotNull(loginDao.get(l.getId()));
		
		
	}
	
}
