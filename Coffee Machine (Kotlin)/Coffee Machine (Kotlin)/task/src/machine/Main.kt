package machine

class CoffeeMachine(var water: Int, var milk: Int, var bean: Int, var cup: Int, var money: Int) {
    fun buy(buyCmd: String) {
        if (buyCmd == "back") return
        if (check(buyCmd) != null) {
            return println("Sorry, not enough ${check(buyCmd)}!")
        }
        if (buyCmd == "1") {
            water -= 250
            bean -= 16
            money += 4
            cup -= 1
        }
        if (buyCmd == "2") {
            water -= 350
            milk -= 75
            bean -= 20
            money += 7
            cup -= 1
        }
        if (buyCmd == "3") {
            water -= 200
            milk -= 100
            bean -= 12
            money += 6
            cup -= 1
        }
        println("I have enough resources, making you a coffee!")
    }

    fun take(): Int {
        println("I gave you \$$money")
        val tmp = money
        money = 0
        return tmp
    }

    fun fill() {
        println("Write how many ml of water you want to add:")
        water += readln().toInt()
        println("Write how many ml of milk you want to add:")
        milk += readln().toInt()
        println("Write how many grams of coffee beans you want to add:")
        bean += readln().toInt()
        println("Write how many disposable cups you want to add:")
        cup += readln().toInt()
    }
    fun remaining() {
        println("The coffee machine has:\n" +
                "$water ml of water\n" +
                "$milk ml of milk\n" +
                "$bean g of coffee beans\n" +
                "$cup disposable cups\n" +
                "\$$money of money")
    }

    fun check(buyCmd: String): String? {
        // For the espresso, 250 ml of water and 16 g of coffee beans. It costs $4.
        // For the latte, 350 ml of water, 75 ml of milk, and 20 g of coffee beans. It costs $7.
        // And for the cappuccino, 200 ml of water, 100 ml of milk, and 12 g of coffee. It costs $6.
        var waterCost = 0
        var milkCost = 0
        var beanCost = 0
        var cupCost = 0
        if (buyCmd == "1") {
            waterCost = 250
            milkCost = 0
            beanCost = 16
            cupCost = 1
        }
        if (buyCmd == "2") {
            waterCost = 350
            milkCost = 75
            beanCost = 20
            cupCost = 1
        }
        if (buyCmd == "3") {
            waterCost = 200
            milkCost = 100
            beanCost = 12
            cupCost = 1
        }
        if (water < waterCost) {
            return "water"
        }
        if (milk < milkCost) {
            return "milk"
        }
        if (bean < beanCost) {
            return "coffee beans"
        }
        if (cup < cupCost) {
            return "disposable cups"
        }
        return null
    }
}

fun main() {
    val numbers = listOf(1,2,3,4)
    var sum = 0
    for (i in 1 until numbers.size) {
        sum += numbers[i]
    }
    val coffeeMachine = CoffeeMachine(
        water = 400,
        milk = 540,
        bean = 120,
        cup = 9,
        money = 550
    )
    var command = ""
    while (command != "exit") {
        println("Write action (buy, fill, take, remaining, exit):")
        command = readln()
        when (command) {
            "buy" -> {
                println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
                val buyCmd = readln()
                coffeeMachine.buy(buyCmd)
            }
            "fill" -> coffeeMachine.fill()
            "take" -> coffeeMachine.take()
            "remaining" -> coffeeMachine.remaining()
        }
    }
}
