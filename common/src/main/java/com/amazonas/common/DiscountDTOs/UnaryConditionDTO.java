package com.amazonas.common.DiscountDTOs;

/***
 *
 * @param unaryConditionType type of condition from the appropriate Enum
 * @param number If <code>unaryConditionType</code> is minimum items from category or product, so the argument
 *               will be the minimum limit. If <code>unaryConditionType</code> is minimum price, so the argument
 *               will be the minimal price for fulfillment of the condition. All cases satisfy
 *               condition>=<code>number</code>.
 * @param text  If <code>unaryConditionType</code> is minimum items from category, so the argument will be category
 *              name. If <code>unaryConditionType</code> is minimum items from product, so the argument will be product
 *              ID. If <code>unaryConditionType</code> is minimum price, so the argument will be meaningless
 */
public record UnaryConditionDTO(UnaryConditionType unaryConditionType,
                                int number,
                                String text)
        implements DiscountConditionDTO{
}
