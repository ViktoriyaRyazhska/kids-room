package ua.softserveinc.tc.search;

import org.springframework.stereotype.Repository;

import ua.softserveinc.tc.constants.SearchConstants;
import ua.softserveinc.tc.entity.Child;

/**
 * Created by edward on 5/16/16.
 */
@Repository
public class ChildSearch extends BaseSearch<Child> {

    {
        searchFields = SearchConstants.childSearchFields;
    }

    @Override
    public Class<Child> getTClass() {
        return Child.class;
    }

}