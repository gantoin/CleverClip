package fr.gantoin.data.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SamplePerson extends AbstractEntity {
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String occupation;
    private String role;
    private boolean important;
}
