## Budget Tracker
Gemaakt door Rick Vinke, Monique Sabong, Thijs Janse

Voor deze applicatie wordt gebruik gemaakt van Kotlin en JavaFX.
Java 21 is vereist om dit programma te draaien.

### Patterns 

- Factory pattern: in de applicatie hebben wij een DAO Factory die wordt gebruikt om instanties van Data Access Object (DAO) klassen te maken. Dit helpt bij het creëren van een abstractielaag tussen de applicatiecode en de database-implementatie. Met behulp van de Factory kunnen we op een gestructureerde manier DAO-objecten aanmaken zonder de details van hun implementatie te kennen. Dit bevordert de modulariteit, flexibiliteit en onderhoudbaarheid van de code, omdat het gemakkelijk is om DAO-implementaties te wijzigen of nieuwe toe te voegen zonder de rest van de code te beïnvloeden.

- Observer pattern: Het Observer-patroon wordt gebruikt om een "publish-subscribe" mechanisme te implementeren, waarbij objecten (observers) automatisch worden geïnformeerd en bijgewerkt over de wijzigingen in een ander object (subject of observable). Dit patroon wordt gebruikt om afhankelijkheden tussen objecten los te koppelen, waardoor een flexibeler en onderhoudbaar ontwerp ontstaat.

    In dit  geval wordt het Observer-patroon gebruikt om te reageren op wijzigingen in de gegevens van de budgetten en gebruikersinformatie. De BudgetDAO en UserInfoModel fungeren als de observables, terwijl de Observer interface en de implementerende klassen (BudgetController, etc.) fungeren als de observers. Wanneer er wijzigingen worden aangebracht in de budgetten of de gebruikersinformatie, worden alle geregistreerde observers automatisch bijgewerkt via de notifyObservers() methode. Dit zorgt voor een losse koppeling tussen de data access layer en de gebruikersinterface, waardoor de code beter onderhoudbaar en uitbreidbaar wordt.

- Command pattern: Het Command-patroon wordt gebruikt om operaties te encapsuleren als objecten, waardoor ze kunnen worden geparametriseerd met verschillende aanvragen, in de wachtrij kunnen worden gezet, worden gelogd en teruggedaan. Dit patroon zorgt voor een scheiding tussen de opdrachtgever (client) die een aanvraag initieert, en de ontvanger (receiver) die de aanvraag uitvoert.

    In dit  geval wordt het Command-patroon gebruikt om de delete-operatie (DELETE FROM ...) voor verschillende database tabellen te encapsuleren. Door het gebruik van een DeleteCommand object kan de delete-operatie worden uitgevoerd met behulp van een uniforme interface, ongeacht de specifieke tabelnaam. Dit verhoogt de modulariteit en flexibiliteit van de code, omdat nieuwe delete-operaties eenvoudig kunnen worden toegevoegd door nieuwe Command klassen te maken en de bestaande infrastructuur te hergebruiken.

    Alle dao klasses gebruiken de DeleteCommand om delete-operaties uit te voeren voor respectievelijk budgetten en gebruikersinformatie. Door deze commando's te gebruiken, wordt de delete-logica gescheiden van de DAO implementatie, waardoor het onderhoud en de uitbreidbaarheid van de code worden vergemakkelijkt.

### Must have veranderingen

Oude must haves:

- Tags kunnen toevoegen. Het categoriseren
- Vaste uitgaven en inkomsten moet je voor de volgende maand kunnen inzien.
- Uitgaven en ingaven kunnen invoeren.
- Herhalende inkomsten en uitgaven kunnen instellen.
- Reminders in kunnen stellen.
- Filteren en sorteren op inkomsten en uitgaven. 
- Een limiet kunnen instellen voor maximale uitgaven.
- Je inkomsten kunnen verdelen in spaarpotjes bijvoorbeeld boodschappen, vrije tijd en vaste lasten. 

Aangepaste must haves:

- Categoriseren van uitkomsten
- Vaste uitgaven moet je voor de volgende maand kunnen inzien.
- Uitgaven kunnen invoeren.
- Herhalende uitgaven kunnen instellen. (Werkt niet helemaal)
- Filteren en sorteren op inkomsten en uitgaven. 
- Een limiet kunnen instellen voor maximale uitgaven.
- Je inkomsten kunnen verdelen in verschillende budgets bijvoorbeeld boodschappen, vrije tijd en vaste lasten.

### Kleine handleiding

#### Budget pagina

Op de budget pagina kan je alle budgets inzien, toevoegen en verwijderen. Wanneer je op add budget toevoeg kan er een budget worden toegevoegd voor bijvoorbeeld 'Boodschappen' met een budget van 200 euro. De currency staat voor nu vast als EUR. 

Wanneer de budget is toegevoegd, komt het in de tabel te staan. In de 1 na laatste kolom komt er een button te staan genaamd "View". Als je hierop klikt, kan je alle uitgaven zien die onder boodschappen valt. In deze pagina kan je uitgaven toevoegen, aanpassen en verwijderen. 

Bij de Budget tabel en de uitgaven tabel kan er gesorteerd en gefiltered worden met behulp van de zoekbalk, Wanneer je op de kolumn titel klikt, kan er gesorteerd worden.

#### Expenses pagina

In de expenses pagina wordt er een lijndiagram weergegeven met alle uitgaven. Achter Total Budgets staat de totale hoeveelheid van alle budgets bij elkaar opgeteld. Achter Current budget staat de totale budget min de uitgaven. 

In de lijndiagram kan je ook aangeven tussen welke twee datums je de uitgaven wilt zien.

#### Planning pagina

Op de planning pagina wordt een kalender weergegeven. Hierbij kan er een reminder aangemaakt worden door te dubbelklikken op een dag of rechts te klikken voor een context menu. Daarnaast kan er een reminder verwijdert worden, dit kan je doen door rechts te klikken op een reminder en dan Delete in het context menu.

#### Overige opmerkingen
- Expense limiet kan aangepast worden in settings.
- In de header is er een item genaamd "Account". Op dit moment kan je er niks mee. Niet gelukt om meerdere accounts te implementeren. 
