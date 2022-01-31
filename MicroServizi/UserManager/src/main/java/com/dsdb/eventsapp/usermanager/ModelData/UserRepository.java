package com.dsdb.eventsapp.usermanager.ModelData;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer>{
    public User getUserByUserNameAndEmail(String UserName, String Email);
    public User getUserByUserNameAndCodice(String UserName, String Codice);
    public User getUserByUserNameAndPassword(String UserName, String Password);
    public User getUserByIdUser(Integer id);
    public User getUserByCodice(String codice);
    public User getUserByUserNameOrEmail(String UserName, String Email);
}
