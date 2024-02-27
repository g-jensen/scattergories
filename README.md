# Scattergories

### Setup
#### System Requirements 

    # Java 1.17

    # Clojure command line
    brew install clojure

    # NPM/Node for running cljs specs
    brew install npm

    # Install node packages
    npm install 

CSS and Javascript need to be compiled:

    # compile just the css once
    clj -M:test:css once

    # compile css whenever style files are changed
    clj -M:test:css auto

    # compile just cljs to javascript once (also runs tests)
    clj -M:test:cljs once

    # compile cljs and run tests when ever a file changes
    clj -M:test:cljs

For production:

    CC_ENV=production clj -M:test:css once
    CC_ENV=production clj -M:test:cljs once

### Database Setup

    # Run the Scattergories database
    bin/db

    # Run Migrations
    clj -M:test:migrate

    # Seed Development Database
    clj -M:test:seed

### Running tests

    # clojure specs:
    clj -M:test:spec

    # clojure specs automatically running when fileds are changed:
    clj -M:test:spec -a
    
    # clojurescript specs
    clj -M:test:cljs once

    # clojurescript specs automatically running when files are changed:
    clj -M:test:cljs auto

    # recompile css & cljs specs automatically when files are changed:
    clj -M:test:dev-

### Development

    # run the server
    clj -M:test:run

    # run the server, specs, and cljs in one process
    clj -M:test:dev

    # start the REPL
    clj -M:repl
     
