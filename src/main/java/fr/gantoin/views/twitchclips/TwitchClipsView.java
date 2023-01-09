package fr.gantoin.views.twitchclips;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.gantoin.data.entity.Clip;
import fr.gantoin.data.entity.DialogAndGrid;
import fr.gantoin.data.entity.ImportedClip;
import fr.gantoin.data.service.ImportedClipService;
import fr.gantoin.data.service.TwitchService;
import fr.gantoin.views.MainLayout;

@PageTitle("Twitch Clips")
@Route(value = "clips", layout = MainLayout.class)
@RolesAllowed("USER")
@Uses(Icon.class)
public class TwitchClipsView extends Div {

    private final TwitchService twitchService;
    private Grid<ImportedClip> layoutImportedClipsGrid;
    private List<Clip> clips = new ArrayList<>();

    private Filters filters;
    private final ImportedClipService importedClipService;

    public TwitchClipsView(TwitchService twitchService, ImportedClipService importedClipService) {
        this.twitchService = twitchService;
        this.importedClipService = importedClipService;
        setSizeFull();
        addClassNames("twitch-clips-view");

        filters = new Filters(this::refreshLayoutImportedClipsGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public class Filters extends Div implements Specification<ImportedClip> {

        private final TextField name = new TextField("Name");
        private final TextField phone = new TextField("Phone");
        private final DatePicker startDate = new DatePicker("Date of Birth");
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> occupations = new MultiSelectComboBox<>("Occupation");
        private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            name.setPlaceholder("First or last name");

            occupations.setItems("Insurance Clerk", "Mortarman", "Beer Coil Cleaner", "Scale Attendant");

            roles.setItems("Worker", "Supervisor", "Manager", "External");
            roles.addClassName("double-width");

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                phone.clear();
                startDate.clear();
                endDate.clear();
                occupations.clear();
                roles.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            Button fromTwitch = new Button("Import from Twitch");
            Span twitch = new Span();
            twitch.setClassName("lab la-twitch");
            fromTwitch.setIcon(twitch);
            fromTwitch.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            fromTwitch.addClassName("twitch-color");
            fromTwitch.addClickListener(e -> {
                DialogAndGrid dialogAndGrid = openDialog(new Dialog(), null);
                dialogAndGrid.getDialog().open();
                Button cancelButton = new Button("Cancel", ec -> dialogAndGrid.getDialog().close());
                Button saveButton = new Button("Import", ec -> {
                    importedClipService.importClips(dialogAndGrid.getGrid().getSelectedItems());
                    dialogAndGrid.getDialog().close();
                    refreshLayoutImportedClipsGrid();
                });
                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                dialogAndGrid.getDialog().getFooter().add(cancelButton);
                dialogAndGrid.getDialog().getFooter().add(saveButton);
            });

            Button fromComputer = new Button("Import from computer");
            Icon image = new Icon("vaadin", "picture");
            fromComputer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            fromComputer.setIcon(image);
            fromTwitch.addClickListener(e -> {
                // TODO
            });

            add(name, phone, createDateRangeFilter(), occupations, roles, actions, fromTwitch, fromComputer);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("From");

            endDate.setPlaceholder("To");

            // For screen readers
            setAriaLabel(startDate, "From date");
            setAriaLabel(endDate, "To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        private void setAriaLabel(DatePicker datePicker, String label) {
            datePicker.getElement().executeJs("const input = this.inputElement;" //
                    + "input.setAttribute('aria-label', $0);" //
                    + "input.removeAttribute('aria-labelledby');", label);
        }

        @Override
        public Predicate toPredicate(Root<ImportedClip> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();
            if (!name.isEmpty()) {
                String lowerCaseFilter = name.getValue().toLowerCase();
                Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        lowerCaseFilter + "%");
                Predicate lastNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        lowerCaseFilter + "%");
                predicates.add(criteriaBuilder.or(firstNameMatch, lastNameMatch));
            }
            if (!phone.isEmpty()) {
                String databaseColumn = "phone";
                String ignore = "- ()";

                String lowerCaseFilter = ignoreCharacters(ignore, phone.getValue().toLowerCase());
                Predicate phoneMatch = criteriaBuilder.like(
                        ignoreCharacters(ignore, criteriaBuilder, criteriaBuilder.lower(root.get(databaseColumn))),
                        "%" + lowerCaseFilter + "%");
                predicates.add(phoneMatch);

            }
            if (startDate.getValue() != null) {
                String databaseColumn = "dateOfBirth";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "dateOfBirth";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }
            if (!occupations.isEmpty()) {
                String databaseColumn = "occupation";
                getPredicates(root, criteriaBuilder, predicates, databaseColumn, occupations.getValue());
            }
            if (!roles.isEmpty()) {
                String databaseColumn = "role";
                getPredicates(root, criteriaBuilder, predicates, databaseColumn, roles.getValue());
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }

        private void getPredicates(Root<ImportedClip> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String databaseColumn, Set<String> value) {
            List<Predicate> occupationPredicates = new ArrayList<>();
            for (String occupation : value) {
                occupationPredicates
                        .add(criteriaBuilder.equal(criteriaBuilder.literal(occupation), root.get(databaseColumn)));
            }
            predicates.add(criteriaBuilder.or(occupationPredicates.toArray(Predicate[]::new)));
        }

        private String ignoreCharacters(String characters, String in) {
            String result = in;
            for (int i = 0; i < characters.length(); i++) {
                result = result.replace("" + characters.charAt(i), "");
            }
            return result;
        }

        private Expression<String> ignoreCharacters(String characters, CriteriaBuilder criteriaBuilder,
                                                    Expression<String> inExpression) {
            Expression<String> expression = inExpression;
            for (int i = 0; i < characters.length(); i++) {
                expression = criteriaBuilder.function("replace", String.class, expression,
                        criteriaBuilder.literal(characters.charAt(i)), criteriaBuilder.literal(""));
            }
            return expression;
        }

    }

    private DialogAndGrid openDialog(Dialog dialog, String lastClipId) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addClassName("dialog");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        horizontalLayout.setPadding(true);
        Button next = new Button("Next");
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        next.addClickListener(e2 -> {
            openDialog(dialog, clips.get(clips.size() - 1).getCursor());
        });
        Button previous = new Button("Previous");
        previous.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        previous.addClickListener(e2 -> {
            openDialog(dialog, clips.get(0).getCursor());
        });
        Grid<Clip> clipGrid = new Grid<>();
        if (lastClipId == null) {
            horizontalLayout.add(next);
            clipGrid = getClipGrid(null);
            dialog.add(clipGrid, horizontalLayout);
        } else {
            dialog.removeAll();
            horizontalLayout.add(previous, next);
            clipGrid = getClipGrid(lastClipId);
            dialog.add(clipGrid, horizontalLayout);
        }
        return new DialogAndGrid(dialog, clipGrid);
    }

    private Grid<Clip> getClipGrid(String lastClipId) {
        Grid<Clip> grid = new Grid<>(Clip.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(createClipRenderer()).setHeader("Clip").setAutoWidth(true);
        grid.addColumn(Clip::getViewCount).setHeader("Views").setAutoWidth(true);
        grid.addColumn(Clip::getCreatedAt).setHeader("Date").setAutoWidth(true);
        grid.addColumn(Clip::getDuration).setHeader("Duration").setAutoWidth(true);
        if (lastClipId == null) {
            clips = twitchService.list(lastClipId);
            grid.setItems(clips);
        } else {
            grid.setItems(twitchService.list(lastClipId));
            clips = twitchService.list(lastClipId);
            grid.setItems(clips);
        }
        grid.setWidth("800px");
        return grid;
    }

    private Renderer<Clip> createClipRenderer() {
        return LitRenderer.<Clip>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "<img src=\"${item.thumbnailUrl}\" alt=\"Clip thumbnail\" style=\"height:100px\"></img>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("thumbnailUrl", Clip::getThumbnailUrl);
    }

    private Component createGrid() {
        layoutImportedClipsGrid = new Grid<>(ImportedClip.class, false);
        layoutImportedClipsGrid.addColumn("id").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("url").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("embedUrl").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("broadcasterId").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("broadcasterName").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("creatorId").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("creatorName").setAutoWidth(true);
        layoutImportedClipsGrid.addColumn("videoId").setAutoWidth(true);
        layoutImportedClipsGrid.setItems(query -> importedClipService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        layoutImportedClipsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        layoutImportedClipsGrid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        return layoutImportedClipsGrid;
    }

    private void refreshLayoutImportedClipsGrid() {
        layoutImportedClipsGrid.getDataProvider().refreshAll();
    }
}
