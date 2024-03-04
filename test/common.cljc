(ns common)

(def test-actions
  {:format-str {:spec {:args {:template :string
                              :values [:any]}
                       :return :str}}
   :map {:spec {:args {:func :any
                       :values [:any]}
                :return [:any]}}
   :arg {:spec {:args {:value :any}
                :return :any}}
   ;; reduce
   ;; filter

   :two-formatted-strs-new-line {:return [:format-str {:template "%s\n%s"
                                                       :values [:format-str :format-str]}]}

   :dear {:return [:format-str {:template "Dear %s"
                                :values [:arg]}]}
   :hi {:return [:format-str {:template "Hi, %s!"
                              :values [:arg]}]}
   :hi-dear {:return [:hi {:values [:dear]}]}
   :bye {:return [:format-str {:template "Bye, %s!"
                               :values [:arg]}]}
   :bye-dear {:return [:bye {:values [:dear]}]}

   :hi-bye-dear-different
   {:return [:format-str {:template "%s\n%s"
                          :values [:hi-dear :bye-dear]}]}

   :hi-dear-bye-dear-single
   {:return [:format-str {:template "%s\n%s"
                          :values [:hi-dear :bye-dear]}]
    :args {:renaming {:values [{:values [{:values [{:value :someone}]}]}
                               {:values [{:values [{:value :someone}]}]}]}}}

   :hi-dear-dear-bye-dear-single
   {:return [:format-str {:template "%s\n%s\n%s"
                          :values [:hi-dear :hi :bye-dear]}]
    :args {:renaming {:values [{:values [{:values [{:value :someone}]}]}
                               {:values [{:value :someone}]}
                               {:values [{:values [{:value :someone}]}]}]}}}

   :hi-many {:return [:map {:func :hi
                            :values [:&args]}]}})

(defn get-test-common-action
  [action-name]
  (get test-actions action-name))
