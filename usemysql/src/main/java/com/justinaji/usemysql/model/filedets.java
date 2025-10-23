package com.justinaji.usemysql.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class filedets {

    @Id
    private String id;      // private, accessed via getter/setter
    private String name;
    private String type;

    @ManyToOne
    @JoinColumn(name = "userid")
    private users uploader;
}

