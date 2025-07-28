package timesheetDuplicate.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) CHECK (email REGEXP '^[\\w-\\.]+@cstech\\.ai$')")
    private String email;

    @NotNull
    private String password;

    @NotNull(message = "role is required")
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<User> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSheet> timesheets = new ArrayList<>();

    @ManyToMany(mappedBy = "teamMembers")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "manager")
    private List<Project> managedProjects = new ArrayList<>();
}
