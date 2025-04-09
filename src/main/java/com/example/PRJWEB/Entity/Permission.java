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
public class Permission {
    @Id
    String name;
    String description;

}
