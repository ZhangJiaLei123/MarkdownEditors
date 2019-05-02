(function () {

  initMobileMenu()
  initNewNavLinks()

  /**
   * Initializes a list of links to mark as "updated" by adding a red dot next to them
   */
  function initNewNavLinks() {
    var linkExpirePeriod = 60 * 24 * 3600 * 1000 // 2 months
    var links = [
      {
        title: 'Learn',
        updatedOn: new Date("Fri Mar 1 2019")
      },
      {
        title: 'Examples',
        updatedOn: new Date("Fri Mar 1 2019")
      }
    ]
    var today = new Date().getTime()
    var updatedLinks = links
      .filter(function (link) {
        return link.updatedOn.getTime() + linkExpirePeriod > today
      })
      .map(function (link) {
        return link.title
      })

    var navLinks = document.querySelectorAll('#nav a.nav-link')
    var newLinks = []
    navLinks.forEach(function (link) {
      if (updatedLinks.indexOf(link.textContent) !== -1) {
        newLinks.push(link)
      }
    })
    newLinks.forEach(function (link) {
      var classes = link.classList
      var linkKey = `visisted-${link.textContent}`
      if (localStorage.getItem(linkKey) || classes.contains('current')) {
        classes.remove('updated-link')
        localStorage.setItem(linkKey, 'true')
      } else {
        classes.add('new')
      }
    })
  }

  /**
   * Mobile burger menu button and gesture for toggling sidebar
   */
  function initMobileMenu () {
    var mobileBar = document.getElementById('mobile-bar')
    var sidebar = document.querySelector('.sidebar')
    var menuButton = mobileBar.querySelector('.menu-button')

    menuButton.addEventListener('click', function () {
      sidebar.classList.toggle('open')
    })

    document.body.addEventListener('click', function (e) {
      if (e.target !== menuButton && !sidebar.contains(e.target)) {
        sidebar.classList.remove('open')
      }
    })

    // Toggle sidebar on swipe
    var start = {}, end = {}

    document.body.addEventListener('touchstart', function (e) {
      start.x = e.changedTouches[0].clientX
      start.y = e.changedTouches[0].clientY
    })

    document.body.addEventListener('touchend', function (e) {
      end.y = e.changedTouches[0].clientY
      end.x = e.changedTouches[0].clientX

      var xDiff = end.x - start.x
      var yDiff = end.y - start.y

      if (Math.abs(xDiff) > Math.abs(yDiff)) {
        if (xDiff > 0 && start.x <= 80) sidebar.classList.add('open')
        else sidebar.classList.remove('open')
      }
    })
  }




})()
