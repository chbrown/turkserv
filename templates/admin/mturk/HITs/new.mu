<ng-include src="'/templates/admin/mturk/nav.html'"></ng-include>
<main ng-controller="adminCreateHITCtrl">
  <h3>Create HIT</h3>

  <section class="box">
    <form ng-submit="sync($event)">
      <label>
        <div><b>Max Assignments</b>
          <span class="help">
            The number of times the HIT can be accepted (by different users) and completed before the HIT becomes unavailable. A single user will only be able to complete the HIT once.
          </span>
        </div>
        <input type="text" ng-model="$storage.hit.MaxAssignments">
      </label>

      <label>
        <div><b>Title</b>
          <span class="help">The title of the HIT. A title should be short and describe the kind of task the HIT contains</span>
        </div>
        <input type="text" ng-model="$storage.hit.Title" style="width: 500px">
      </label>

      <label>
        <div><b>Description</b>
          <span class="help">2000 character max</span>
        </div>
        <input type="text" ng-model="$storage.hit.Description" style="width: 500px">
      </label>

      <label>
        <div><b>Reward</b>
          <span class="help">The amount of money (in USD) the Requester will pay a Worker for successfully completing the HIT</span>
        </div>
        <input type="text" ng-model="$storage.hit.Reward">
      </label>

      <label>
        <div><b>Keywords</b>
          <span class="help">One or more words or phrases that describe the HIT, separated by commas</span>
        </div>
        <input type="text" ng-model="$storage.hit.Keywords" style="width: 500px">
      </label>

      <label>
        <div><b>Assignment Duration (e.g., 3h)</b>
          <span class="help">The amount of time that a Worker has to complete the HIT after accepting it</span>
        </div>
        <input type="text" ng-model="$storage.hit.AssignmentDuration">
      </label>

      <label>
        <div><b>Lifetime (e.g., 3d)</b>
          <span class="help">The amount of time that a HIT can be accepted; after the lifetime expires, the HIT no longer appears in searches</span>
        </div>
        <input type="text" ng-model="$storage.hit.Lifetime">
      </label>

      <label>
        <div><b>Auto-approval Delay (e.g., 60m)</b>
          <span class="help">The amount of time after a HIT has been submitted before the assignment is automatically approved</span>
        </div>
        <input type="text" ng-model="$storage.hit.AutoApprovalDelay">
      </label>

      <label>
        <div><b>Other settings</b>
          <span class="help">JSON representation of any other settings to send.
            This is an object that will be merged with the rest of the payload.
            E.g., to require Master's qualification in production, use this:
            <code><pre>
              {
                "QualificationRequirement": {
                  "QualificationTypeId": "2F1QJWKUDD8XADTFD2Q0G6UTO95ALH",
                  "Comparator": "Exists"
                }
              }
            </pre></code>
          </span>
        </div>
        <textarea json-transform enhance ng-model="$storage.extra"
          class="code" style="width: 500px; min-height: 50px;"></textarea>
      </label>

      <p></p>

      <label>
        <div><b>External URL</b>
          <span class="help">The URL of your web form, to be displayed in a frame in the Worker's web browser</span>
        </div>
        <input type="text" ng-model="$storage.hit.ExternalURL" style="width: 500px">
      </label>

      <label>
        <div><b>Frame Height (integer)</b>
          <span class="help">The height of the frame, in pixels</span>
        </div>
        <input type="text" ng-model="$storage.hit.FrameHeight">
      </label>

      <p><button>Create HIT</button></p>
    </form>
  </section>

  <h3>
    <label>Preview
      <input type="checkbox" ng-model="$storage.preview_iframe">
    </label>
  </h3>
  <section class="box" ng-if="$storage.preview_iframe && preview_url">
    <iframe src="{{preview_url}}"
      scrolling="auto" width="100%" frameborder="0" align="center" height="{{$storage.hit.FrameHeight}}"></iframe>
  </section>
</main>