package com.justinaji.jproj.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class chats {
    @Id
    private String c_id;
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private users createdBy;    
}
 