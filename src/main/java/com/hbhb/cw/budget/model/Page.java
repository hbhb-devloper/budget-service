package com.hbhb.cw.budget.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 4336877205161477155L;

    private List<T> list;
    private Long count;
}
