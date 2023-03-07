import org.jsoup.Jsoup

//The Main Function that runs the program
fun main(args: Array<String>) {
    var event = selectEvent()
    event.getRiders()
    event.printTable()
    event.makeSelections()
    event.printSelections()
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
}
//There are 2 event classes. The user needs to pick which one they are interested in.
fun selectEvent():Event {
    val event1: String = "450"
    val event2: String = "250"

    //Create a menu to display the options to the user
    println("Event Classes:")
    println("1. ${event1}sx")
    println("2. ${event2}sx \n")
    print("Choose and event class: ")

    //Validate the input. If it is invalid restart this function. Return an instance of an Event object
    val event = when (readln()!!) {
        "1" -> Event(event1)
        "2" -> Event(event2)
        event1 -> Event(event1)
        event2 -> Event(event2)
        else -> {
            println("Invalid input!\n")
            selectEvent()
        }
    }
    return event
}


//A class to hold the event information like riders and results
//Pass in the event class to make sure we get the right info
class Event(event: String) {
    private val eventClass = event
    private var riders: MutableList<Rider> = mutableListOf()
    private val picks: MutableList<Rider> = mutableListOf()

    //We need to know which riders will be participating in the event
    public fun getRiders() {

        //Parse the html to get the info that we need
        val url = "https://www.supercrosslive.com/riders/${eventClass}"
        val doc = Jsoup.connect(url).get()
        val info = doc.getElementsByClass("driver-list-section").select("a")

        //for each rider found on the page create an instance of a Rider and add it to a list of riders
        info.forEach {
            val riderURL = "https://www.supercrosslive.com${it.attr("href")}"
            riders.add(createRider(riderURL))
            }
        }

    //Create an instance of a rider. Assign the attributes values based on the HTML. Return a Rider object
    private fun createRider(url: String):Rider {
        val rider = Rider()
        val riderPage = Jsoup.connect(url).get()
        rider.name = riderPage.getElementsByClass("rider-title").text()
        rider.number = riderPage.getElementsByClass("profile-photo").text()
        val races = riderPage.getElementsByClass("driver-table")[1].select("tr")
        if (races.size >= 2) {
            rider.lastrace = races[races.size-1].select("td:nth-child(6)").text()
        }
        else {
            rider.lastrace = "-"
        }
        rider.standing = riderPage.getElementsByClass("driver-table")[0].select("tr:nth-child(1)").select("td:nth-child(3)").text()
        val info = riderPage.getElementsByClass("listing-items").select("p")

        return rider
    }

    //Print a human readable table showing each rider and their info
    fun printTable() {
        println("|**********************************************************|")
        println("| ID | Rider                   |  #  | Last Race | Overall |")

        riders.forEachIndexed(){ index, rider ->
            println("|----------------------------------------------------------|")
            print("|${" ".repeat(3-(index+1).toString().length)}${index+1} " )
            print("| ${rider.name}${" ".repeat(24-rider.name.length)}")
            print("|${" ".repeat(4-rider.number.length)}${rider.number} ")
            print("|${" ".repeat(10-rider.lastrace.length)}${rider.lastrace} ")
            println("|${" ".repeat(8-rider.standing.length)}${rider.standing} |")
        }
        println("|**********************************************************|")
    }

    //Get the user's top 10 picks. Validate the input after each one.
    fun makeSelections() {
        println("Pick your favorites!")
        println("Enter the rider ID's for your top ten choices.")

        print("\n1st:  ")
        validateSelection(readln()!!, "1st")
        print("2nd:  ")
        validateSelection(readln()!!, "2nd")
        print("3rd:  ")
        validateSelection(readln()!!, "3rd")
        print("4th:  ")
        validateSelection(readln()!!, "4th")
        print("5th:  ")
        validateSelection(readln()!!, "5th")
        print("6th:  ")
        validateSelection(readln()!!, "6th")
        print("7th:  ")
        validateSelection(readln()!!, "7th")
        print("8th:  ")
        validateSelection(readln()!!, "8th")
        print("9th:  ")
        validateSelection(readln()!!, "9th")
        print("10th: ")
        validateSelection(readln()!!, "10th")
    }

    //Make sure the input is a number within the limits of the list.
    //A rider can't place first and third. Make sure there are no duplicate picks.
    //If there are any issues restart the validation on that pick until it passes.
    private fun validateSelection(rider: String, pick: String) {
        if (rider.toIntOrNull() == null || rider.toInt() !in 1..riders.size) {
            println("Invalid Input!")
            print("${pick}: ")
            validateSelection(readln()!!, pick)
        }
        else if (riders[rider.toInt()-1] in picks) {
            println("You've already picked this rider. Try again.")
            print("${pick}: ")
            validateSelection(readln()!!, pick) //I really like recursion. It makes things like validation simple.
        }
        else {
            picks.add(riders[rider.toInt()-1])
        }
    }

    //Display a table showing the user's top 10 picks.
    public fun printSelections() {
        println("Your picks are:")
        println("|**************************************|")
        println("| PICK | Rider                   |  #  |")

        picks.forEachIndexed(){ index, rider ->
            println("|--------------------------------------|")
            print("|${" ".repeat(4-(index+1).toString().length)}${index+1}  " )
            print("| ${rider.name}${" ".repeat(24-rider.name.length)}")
            println("|${" ".repeat(4-rider.number.length)}${rider.number} |")
        }
        println("|**************************************|")
    }
}

//A class to hold information about a rider. This class has no methods, only attributes.
data class Rider (
    var name: String = "Name",
    var number: String = "",
    var lastrace: String = "-",
    var standing: String = " "
)