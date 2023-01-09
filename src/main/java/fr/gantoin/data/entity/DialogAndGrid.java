package fr.gantoin.data.entity;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DialogAndGrid {
    private Dialog dialog;
    private Grid<Clip> grid;
}
