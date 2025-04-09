package com.example.PRJWEB.Entity;

import com.example.PRJWEB.Enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Builder
public class Role {
    @Id
    String name;
    String description;

}
