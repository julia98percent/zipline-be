package com.zipline.entity.publicitem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cortarNo;

    private String cortarName;
    private Double centerLat;
    private Double centerLon;
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore // Prevent serialization of the parent to avoid recursion
    private Region parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent serialization of children to avoid recursion
    private List<Region> children;
}
