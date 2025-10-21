package com.justinaji.newproj.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor //makes the constructor and neccessary get methods for the class automatically
@NoArgsConstructor
@Entity
public class filedets {
    @Id
    public String id;
    public String name;
    public String type;

}
