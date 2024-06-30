package com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs;

/***
 * Representing a simple discount - The level of generality of the discount, and the percentage of the discount
 * @param hierarchyLevel An Enum that says whether the discount is at the product/category/store level
 * @param text If the <code>hierarchyLevel</code> is product, so the argument must be the product ID.
 *             If the <code>hierarchyLevel</code> is category, so the argument must be the category name.
 *             If the <code>hierarchyLevel</code> is store, so the argument is meaningless.
 */
public record SimpleDiscountDTO(HierarchyLevel hierarchyLevel,
                                String text,
                                int percent)
        implements DiscountComponentDTO {
}
