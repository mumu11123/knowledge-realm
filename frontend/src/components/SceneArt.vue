<script setup lang="ts">
withDefaults(defineProps<{ scene?: string }>(), { scene: 'library' })
</script>
<template>
  <svg
    class="scene-art"
    viewBox="0 0 400 230"
    fill="none"
    role="img"
    :aria-label="
      scene === 'cafe'
        ? '咖啡馆里叠放的托盘'
        : scene === 'station'
          ? '森林小站的取餐窗口'
          : '星光书屋的书架与灯光'
    "
  >
    <defs>
      <linearGradient id="sky" x2="1" y2="1">
        <stop stop-color="#e2deff" />
        <stop offset="1" stop-color="#c6d9f7" />
      </linearGradient>
      <linearGradient id="ground" x2="1" y2="1">
        <stop stop-color="#aba0df" />
        <stop offset="1" stop-color="#d5caef" />
      </linearGradient>
    </defs>
    <rect width="400" height="230" rx="24" fill="url(#sky)" />
    <circle cx="324" cy="47" r="24" fill="#fff5d9" />
    <circle cx="68" cy="38" r="3" fill="white" />
    <path d="m268 28 3 7 7 3-7 3-3 7-3-7-7-3 7-3z" fill="white" />
    <path d="M0 193Q100 170 200 194t200-5v41H0z" fill="url(#ground)" />
    <g v-if="scene === 'library'">
      <rect x="108" y="51" width="183" height="142" rx="9" fill="#fff9ee" />
      <path d="M120 61h159v122H120z" fill="#ded5f2" />
      <path d="M120 101h159M120 145h159" stroke="#a58bc5" stroke-width="7" />
      <path
        d="M133 70v26m18-32v32m18-22v22m46-31v31m18-27v27m18-20 8 20M135 111v29m20-24v24m18-28v28m48-22v22m19-29v29m17-21 6 21M133 156v23m20-25v25m19-21v21m49-27v27m18-20v20"
        stroke="#8773be"
        stroke-width="11"
      />
      <path d="M188 67v29m-1 16v28m0 16v23" stroke="#e8ac99" stroke-width="11" />
      <path d="M62 192V91m-20 0h40L71 66H53z" stroke="#8370ad" stroke-width="5" fill="#fff1c6" />
      <path
        d="M316 190v-39m0 22c-27-3-27-24-18-26 12-3 18 20 18 26m1-10c24-7 26-26 17-27-12-1-17 20-17 27"
        stroke="#759e9a"
        stroke-width="4"
        fill="#98bcb0"
      />
    </g>
    <g v-else-if="scene === 'cafe'">
      <rect x="53" y="60" width="94" height="88" rx="30" fill="#f8f0e8" />
      <path d="M100 62v84M56 103h88" stroke="#beb1d5" stroke-width="5" />
      <rect x="73" y="175" width="260" height="14" rx="7" fill="#9c80bb" />
      <path d="M100 189v28m207-28v28" stroke="#9c80bb" stroke-width="9" />
      <rect x="174" y="147" width="115" height="14" rx="7" fill="#bc9ed4" />
      <rect x="174" y="129" width="115" height="14" rx="7" fill="#a0badd" />
      <rect x="174" y="111" width="115" height="14" rx="7" fill="#f7e8bb" />
      <path d="M167 170h127" stroke="#f9f4eb" stroke-width="10" stroke-linecap="round" />
      <path d="M314 132h23v29h-23z" fill="#f9f4eb" />
      <path d="M337 135c19-3 19 23 0 20" stroke="#f9f4eb" stroke-width="5" />
      <path d="M323 120q-7-9 0-18" stroke="white" stroke-width="4" stroke-linecap="round" />
    </g>
    <g v-else>
      <path d="m77 78 112-40 112 40v14H77z" fill="#8d78b5" />
      <rect x="91" y="91" width="196" height="100" rx="5" fill="#f6eee5" />
      <rect x="113" y="108" width="145" height="47" rx="7" fill="#b7b0d4" />
      <path d="M107 159h160" stroke="#9d87b9" stroke-width="7" />
      <path
        d="M52 187v-67m0 24-18 23m18-37 20 20m269 38v-74m0 31-23 19m23-34 18 17"
        stroke="#739f9a"
        stroke-width="6"
        stroke-linecap="round"
      />
      <circle cx="147" cy="174" r="10" fill="#c6a0ca" />
      <circle cx="181" cy="174" r="10" fill="#9eb6d7" />
      <circle cx="215" cy="174" r="10" fill="#e6bd92" />
      <path
        d="M135 202v-12q12-12 24 0v12m10 0v-12q12-12 24 0v12m10 0v-12q12-12 24 0v12"
        fill="#a99ac8"
      />
    </g>
    <ellipse cx="200" cy="216" rx="112" ry="4" fill="#b4a7ce" opacity=".3" />
  </svg>
</template>
