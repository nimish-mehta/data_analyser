####To run:####
    ShellRunner

####To run manual test queries:####
    Test

Note: Tested only on JDK8 Intellij-Idea OSX

####Shell Commands (Do not work during initial interaction)####
    1. exit - to quit
    2. clear - to reset the collection state excluding the sort key
    3. sort:<key> - to change the sort key
    4. _data - print the data

###Design Overview:###
    parse:
        condition package contains the logic of query operators
        Query takes a String as input and converts it into conditional 
        operators
    Model:
        Collection defines generic collection behaviour using java reflection
        and templating.
        ComparatorByFieldName can be used to compare two objects 
        with a given field name.
    Controller:
        CommandManager provides action over the collection via a executeCommand method
    View:
        shell implements a simple shell and contains the main loop of
        the program
        ShellRunner provides main method for initialisation and running the shell.
    Test:
        test.py contains simple integration test for collection.


###Assumptions Made:###
    1. Query Syntax supports three logical operators AND, OR, NOT.
       All operators will be in lowercase
        Precedence:
            NOT > AND > OR
        Type of operator:
            NOT: Unary
            AND: Binary
            OR: Binary
    2. When using two operator together separate by bracket.
        i.e (a=b or (not d=e) (a=b or not d=e)
    3. Data is loaded from data package file which is always present.
    this could be replace by proper data parser.
    4. All data is currently in string format.
    5. The fields in the file specified were mapped to the class in the
    following manner:
        name => name
        brand => brand
        color => color
        in_stock => availability
    6. Sortkey is always set after loading some data. 
    (not doing so currently leads to and exception)
    7. comparisons are case sensitive brand != BRAND

    Note: url is currently ignored as it was not mentioned in the
    problem statement.
    However, it can be read and set as property in the model class
    for full support for all operations on it.
