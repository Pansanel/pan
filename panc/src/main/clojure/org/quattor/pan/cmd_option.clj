(ns org.quattor.pan.cmd-option
  (:use [org.quattor.pan.cmd-option-utils]
        [clojure.string :only [split blank? join]]
        [clojure.java.io :only [as-file]])
  (:require [org.quattor.pan.settings :as settings])
  (:import (org.quattor.pan.output TxtFormatter 
                                   JsonFormatter
                                   JsonGzipFormatter
                                   DotFormatter 
                                   PanFormatter
                                   PanGzipFormatter
                                   XmlFormatter
                                   XmlGzipFormatter
                                   XmlDBFormatter
                                   DepFormatter)
           (org.quattor.pan CompilerOptions$DeprecationWarnings)))

(declare process)

(defn to-settings [cli-options]
  (settings/merge-defaults (into {} (mapcat process cli-options))))

(defn str->formatters
  "Returns map with vector of formatters from comma-separated list of formatter names."
  [s]
  (let [names (split s #"\s*,\s*")]
    {:formatter 
     (reduce
       (fn [v name]
         (case name
           "text" (conj v (TxtFormatter/getInstance))
           "json" (conj v (JsonFormatter/getInstance))
           "json.gz" (conj v (JsonGzipFormatter/getInstance))
           "dot" (conj v (DotFormatter/getInstance))
           "pan" (conj v(PanFormatter/getInstance))
           "pan.gz" (conj v (PanGzipFormatter/getInstance))
           "xml" (conj v(XmlFormatter/getInstance))
           "xml.gz" (conj v (XmlGzipFormatter/getInstance))
           "xmldb" (conj v (XmlDBFormatter/getInstance))
           "dep" (conj v (DepFormatter/getInstance))
           "none" v
           (throw (Exception. (str "unknown formatter: " name)))))
       #{}
       names)}))

(defmulti process
  "Process a command line option given the name and
   string value passed in.  Returns validated and
   updated value.  The dispatch value is the parameter
   name as a keyword."
  (fn [[k v]] (keyword k)))

(defmethod process :default
  [[k v]]
  {(keyword k) v})

(defmethod process :debug
  [[k v]]
  (if v
    {:debug-exclude-patterns []
     :debug-include-patterns [#".*"]}
    {}))

(defmethod process :debug-ns-include
  [[k v]]
  {:debug-include-patterns [(re-pattern v)]})

(defmethod process :debug-ns-exclude
  [[k v]]
  {:debug-exclude-patterns [(re-pattern v)]})

(defmethod process :include-path
  [[k v]]
  (let [paths (split-on-commas v)
        dirs (map absolute-file paths)
        bad-dirs (filter (complement directory?) dirs)]
    (if (= 0 (count bad-dirs))
      {(keyword k) dirs}
      (throw (Exception. (str 
                           "include path must contain only existing directories: " 
                           (join " " bad-dirs)))))))

(defmethod process :output-dir
  [[k v]]
  (let [d (absolute-file v)
        ok? (directory? d)]
    (if ok?          
      {(keyword k) d}
      (throw (Exception. (str name " must be an existing directory"))))))

(defmethod process :formats
  [[k v]]
  (str->formatters v))

(defmethod process :max-iteration
  [[k v]]
  (positive-integer (keyword k) v))

(defmethod process :max-recursion
  [[k v]]
  (positive-integer (keyword k) v))

(defmethod process :logging
  [[k v]]
  {(keyword k) (split-on-commas v)})

(defmethod process :log-file
  [[k v]]
  {(keyword k) (absolute-file v)})

(defmethod process :warnings
  [[k v]]
  (let [switches {"off" {:warnings CompilerOptions$DeprecationWarnings/OFF}
                  "on" {:warnings CompilerOptions$DeprecationWarnings/ON}
                  "fatal" {:warnings CompilerOptions$DeprecationWarnings/FATAL}}]
    (if-let [switch (switches v)]
      switch
      (throw (Exception. (str k " value must be off, on, or fatal"))))))
