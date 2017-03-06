# Simple ToDo List Manager

This application is a Simple ToDo List Manager that allows users to add, complete, uncomplete, and delete todo items.  The todo items as well as any updates or deletions are also sent to the server to persist in an in-memory instance of Datomic.  These items do not persist after server is shutdown.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Installation

To build the application, run:

    lein uberjar

Ensure that there is a profiles.clj file at the root of the project that contains:

    {:profiles/dev  {:env {:database-url "datomic:mem://todo-dev"}}
     :profiles/test {:env {:database-url "datomic:mem://todo-test"}}}

## Usage

FIXME: explanation

    $ java -Ddatabase-url="datomic:mem://todo" -jar target/uberjar/dwace.jar

## Running

To start a web server for the application, run:

    lein run
		
After web server starts, to connect a REPL to the web client, run: 

    lein figwheel 
		
NOTE: On Unix (such as OS X) systems or Linux systems, the use of [rlwrap](https://github.com/hanslub42/rlwrap) will provide command line history and other niceties for your fighweel repl that one is used to with the nrepl.  On OS X it can be installed using the [Homebrew](http://brew.sh) package manager.  To use:

    rlwrap lein figwheel		
		
To connect a REPL to the server, run:

    lein repl :connect 7000

## License
Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
