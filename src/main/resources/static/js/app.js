// Active nav highlighting based on current path
(function(){
  const path = location.pathname;
  const map = [
    {key:'home', match: /^\/$/},
    {key:'dashboard', match: /^\/dashboard/},
    {key:'patients', match: /^\/patients/},
    {key:'appointments', match: /^\/appointments/},
    {key:'doctor', match: /^\/doctor/},
    {key:'settings', match: /^\/settings/}
  ];
  const active = map.find(m => m.match.test(path));
  if(active){
    const link = document.querySelector(`a[data-nav="${active.key}"]`);
    if(link){ link.classList.add('active'); }
  }
})();

// Simple ripple effect for buttons
(function(){
  document.addEventListener('click', function(e){
    const target = e.target.closest('.btn, .nav-link.pill');
    if(!target) return;
    const rect = target.getBoundingClientRect();
    const ripple = document.createElement('span');
    ripple.className = 'ripple';
    const size = Math.max(rect.width, rect.height);
    ripple.style.width = ripple.style.height = size + 'px';
    ripple.style.left = (e.clientX - rect.left - size/2) + 'px';
    ripple.style.top  = (e.clientY - rect.top  - size/2) + 'px';
    target.appendChild(ripple);
    setTimeout(()=> ripple.remove(), 600);
  });
})();
