package com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs;

/***
 *
 * @param simpleConditionType type of condition from the appropriate Enum
 * @param number If <code>simpleConditionType</code> is minimum items from category or product, so the argument
 *               will be the minimum limit. If <code>simpleConditionType</code> is minimum price, so the argument
 *               will be the minimal price for fulfillment of the condition. All cases satisfy
 *               condition>=<code>number</code>.
 */
public record SimpleConditionDTO(SimpleConditionType simpleConditionType,
                                 int number)
        implements DiscountConditionDTO{
}
